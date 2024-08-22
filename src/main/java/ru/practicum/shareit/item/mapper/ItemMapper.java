package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDatesDto toItemDatesDto(Item item, Booking lastBooking, Booking nextBooking) {
        return new ItemDatesDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? BookingMapper.toBookingDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDto(nextBooking) : null);
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder().id(itemDto.getId()).name(itemDto.getName()).description(itemDto.getDescription())
                .available(itemDto.getAvailable()).build();
    }
}
