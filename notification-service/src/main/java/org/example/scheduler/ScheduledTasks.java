package org.example.scheduler;


import org.example.service.NotificationService;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final NotificationService notificationService;

    public ScheduledTasks(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void retrySendFailedMessage() {
        log.info("Начало обработки неотправленных сообщений на почту пользователей");
        try {
            notificationService.checkNotSendEvents();
        } catch (DataException e) {
            log.error("Ошибка при работе с БД", e);
        } catch (DataAccessException e) {
            log.error("Ошибка при подключении к БД", e);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        } finally {
            log.info("Процесс обработки неотправленных сообщений на почту пользователей закончен.");
        }
    }

}
