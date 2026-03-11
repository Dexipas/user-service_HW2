package org.example.userservice.events;

import jakarta.validation.constraints.Email;

public record UserCreatedEvent (
        @Email
        String email
) {
}
