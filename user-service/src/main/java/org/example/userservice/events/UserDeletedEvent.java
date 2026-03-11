package org.example.userservice.events;

import jakarta.validation.constraints.Email;

public record UserDeletedEvent (
        @Email
        String email
) {
}
