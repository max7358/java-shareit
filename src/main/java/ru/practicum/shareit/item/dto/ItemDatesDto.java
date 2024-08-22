package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemDatesDto extends ItemDto {
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    public ItemDatesDto(Long id, @NotEmpty String name, @NotEmpty String description, @NotNull Boolean available, Long request, BookingDto lastBooking, BookingDto nextBooking) {
        super(id, name, description, available, request);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
