package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    @NotBlank
    @Email
    private String email;
    private String name;
}