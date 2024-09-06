package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor().getId(), itemRequest.getCreated(), List.of());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor().getId(), itemRequest.getCreated(), items);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, UserDto userDto) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(),
                UserMapper.toUser(userDto), itemRequestDto.getCreated());
    }
}
