package org.example.userservice.dto;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @Min(1)
        @Max(100)
        Integer age,
        LocalDateTime createdAt
) {
        public static UserDTO create(String name, String email, Integer age) {
                return new UserDTO(null, name, email, age, null);
        }

        public static UserDTO withId(UUID id, String name, String email, Integer age){
                return new UserDTO(id, name, email, age, null);
        }

        public static UserDTO exists(UUID id, String name, String email, Integer age, LocalDateTime localDateTime) {
                return new UserDTO(id, name, email, age, null);
        }

}
