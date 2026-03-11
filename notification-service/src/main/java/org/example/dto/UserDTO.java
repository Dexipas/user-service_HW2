package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserDTO(UUID id,
                      @NotBlank
                      String name,
                      @NotBlank
                      @Email
                      String email,
                      @Min(1)
                      @Max(100)
                      Integer age) {
}
