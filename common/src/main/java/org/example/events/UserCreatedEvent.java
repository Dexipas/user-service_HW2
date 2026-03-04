package org.example.events;

import jakarta.validation.constraints.Email;

public record UserCreatedEvent (
        @Email
        String email
) {
}
