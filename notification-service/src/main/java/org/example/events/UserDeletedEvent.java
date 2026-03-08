package org.example.events;

import jakarta.validation.constraints.Email;

public record UserDeletedEvent (
        @Email
        String email
) {
}
