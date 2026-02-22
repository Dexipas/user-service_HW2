package org.example.integrations;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import org.example.App;
import org.example.repository.EventRepository;
import org.example.service.NotificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = App.class)
public class EmailTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "pas"))
            .withPerMethodLifecycle(false);

    @DynamicPropertySource
    static void configureMailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "127.0.0.1");
        registry.add("spring.mail.port", ServerSetupTest.SMTP::getPort);
        registry.add("spring.mail.username", () -> "test");
        registry.add("spring.mail.password", () -> "pas");
        registry.add("spring.mail.protocol", () -> "smtp");

    }
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EventRepository eventRepository;


    @Test
    void shouldSendEmail() throws MessagingException, jakarta.mail.MessagingException, IOException {
        String email = "test@mail.com";
        String text = "Hello world!";

        notificationService.sendMessage(email, text);

        Message[] receivedMessage = greenMail.getReceivedMessages();
        assertThat(receivedMessage).hasSize(1);
        assertThat(receivedMessage[0].getContent().toString().trim()).isEqualTo(text);

    }
}

