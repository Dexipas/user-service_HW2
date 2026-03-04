package org.example.model;

import jakarta.persistence.*;
import org.example.status.Status;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String email;
    @Column(name = "attempts_to_send")
    private Integer attemptsToSend;
    @UpdateTimestamp
    @Column(name = "recent_attempt")
    private LocalDateTime recentAttempt;

    public Event() {

    }

    public Event(UUID id, String title, Status status, String email, Integer attemptsToSend) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.email = email;
        this.attemptsToSend = attemptsToSend;
    }

    public Event(String title, Status status, String email, Integer attemptsToSend) {
        this.title = title;
        this.status = status;
        this.email = email;
        this.attemptsToSend = attemptsToSend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAttemptsToSend() {
        return attemptsToSend;
    }

    public void setAttemptsToSend(Integer attemptsToSend) {
        this.attemptsToSend = attemptsToSend;
    }
}
