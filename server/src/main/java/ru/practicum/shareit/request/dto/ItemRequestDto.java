package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private ZonedDateTime created;
}
