package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDtoConverter {
    ItemDto toDto(Item item);

    Item fromDto(long sharerId, ItemDto itemDto);

    ItemDtoWithBookingAndComments toDtoWithBookingAndComments(Item item,
                                                              BookingDtoOnlyIdAndBookerId lastBooking,
                                                              BookingDtoOnlyIdAndBookerId nextBooking,
                                                              List<CommentDto> comments);
}
