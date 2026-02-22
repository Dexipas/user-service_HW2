package org.example.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic userEventTopic() {
        return TopicBuilder.name("user-event")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
