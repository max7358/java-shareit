package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemAllDto extends ItemDto {
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    public ItemAllDto(Long id, String name, String description, Boolean available,
                      Long requestId, Long ownerId, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        super(id, name, description, available, requestId, ownerId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
