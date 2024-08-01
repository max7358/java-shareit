package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    private String name;
}
