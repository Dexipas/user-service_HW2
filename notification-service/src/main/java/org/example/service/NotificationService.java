package org.example.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import org.example.dto.UserDTO;
import org.example.events.UserCreatedEvent;
import org.example.events.UserDeletedEvent;
import org.example.feign.UserClient;
import org.example.model.Event;
import org.example.repository.EventRepository;
import org.example.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

@KafkaListener(topics = "user-event", groupId = "notification-service-group")
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender javaMailSender;
    private final EventRepository eventRepository;
    private final UserClient userClient;
    private final String EVENT_CREATE = "CREATE";
    private final String EVENT_DELETE = "DELETE";
    private final String TEXT_MESSAGE_CREATE = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
    private final String TEXT_MESSAGE_DELETE = "Здравствуйте! Ваш аккаунт был удалён.";

    private final Integer MAX_NUM_FAIL = 5;
    private final Integer ATTEMPTS_IS_NULL = 0;

    public NotificationService(JavaMailSender javaMailSender, EventRepository eventRepository, UserClient userClient) {
        this.javaMailSender = javaMailSender;
        this.eventRepository = eventRepository;
        this.userClient = userClient;
    }

    public void sendMessage(String email, String textMessage) {
        log.info("Инициируется отправка сообщения на {} ", email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("User Service");
        message.setText(textMessage);
        message.setFrom("notification@usersevice.com");

        javaMailSender.send(message);
    }

    @KafkaHandler
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        Event event = new Event(EVENT_CREATE, Status.NOT_SEND, userCreatedEvent.email(), ATTEMPTS_IS_NULL);

        try {
            var response = userClient.findByEmail(userCreatedEvent.email());
            UserDTO user = response.getContent();
            log.info("Пользователь {} присутствует в БД user-service.", user.email());
        } catch (FeignException.NotFound e) {
            log.error("Пользователя {} нету в БД при событии его создания!", userCreatedEvent.email());
        } catch (FeignException e) {
            log.error("Ошибка с подключением к user-service: {}", e.status());
        }

        eventRepository.save(event);
        log.info("Событие {} с пользователем с email {} успешно сохранено в БД", EVENT_CREATE, userCreatedEvent.email());
    }

    @KafkaHandler
    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        Event event = new Event(EVENT_DELETE, Status.NOT_SEND, userDeletedEvent.email(), ATTEMPTS_IS_NULL);

        try {
            var response = userClient.findByEmail(userDeletedEvent.email());
            UserDTO user = response.getContent();
            log.error("Пользователь {} присутствует в БД user-service, хотя должен быть удален!", user.email());
        } catch (FeignException.NotFound e) {
            log.info("Пользователя {} нету в БД", userDeletedEvent.email());
        } catch (FeignException e) {
            log.error("Ошибка с подключением к user-service: {}", e.status());
        }

        eventRepository.save(event);
        log.info("Событие {} с пользователем с email {} успешно сохранено в БД", EVENT_DELETE, userDeletedEvent.email());
    }

    public void checkNotSendEvents() {
        List<Event> eventsNotSend = eventRepository.findByStatus(Status.NOT_SEND);
        for(Event event: eventsNotSend) {
            if (!event.getAttemptsToSend().equals(MAX_NUM_FAIL)) {
                try {
                    switch (event.getTitle()) {
                        case EVENT_CREATE -> sendMessage(event.getEmail(), TEXT_MESSAGE_CREATE);
                        case EVENT_DELETE -> sendMessage(event.getEmail(), TEXT_MESSAGE_DELETE);
                    }
                    log.info("Сообщение с событием {} успешно отправлено на {}", event.getTitle(), event.getEmail());
                    event.setStatus(Status.SEND);
                    eventRepository.save(event);
                } catch (MailException e) {
                    int numAttempts = event.getAttemptsToSend() + 1;
                    event.setAttemptsToSend(numAttempts);
                    eventRepository.save(event);
                    log.error("Ошибка при отправке сообщения на {}. Cчетчик неудачных попыток повышен на 1", event.getEmail());
                    log.error("Исключение", e);
                }
            }
            else {
                event.setStatus(Status.FAIL);
                eventRepository.save(event);
                log.error("Превышено число попыток на отправку сообщения на {}. Смена статуса на {}",event.getEmail(), Status.FAIL.toString());
            }
        }
    }
}