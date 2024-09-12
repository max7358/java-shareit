package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getOwner() != null ? item.getOwner().getId() : null
        );
    }

    public static ItemAllDto toItemAllDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> commentsDto) {
        return new ItemAllDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getOwner().getId(), lastBooking, nextBooking, commentsDto);
    }

    public static Item toItem(ItemDto itemDto, UserDto userDto) {
        return Item.builder().id(itemDto.getId()).name(itemDto.getName()).description(itemDto.getDescription())
                .available(itemDto.getAvailable()).owner(UserMapper.toUser(userDto)).build();
    }
}
