package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDtoConverter {
    ItemDto toDto(Item item);

    Item fromDto(ItemDto itemDto, User owner);

    Item fromDtoInput(ItemDtoInput itemDto, User owner);

    ItemDtoWithBookingAndComments toDtoWithBookingAndComments(Item item,
                                                              BookingDtoOnlyIdAndBookerId lastBooking,
                                                              BookingDtoOnlyIdAndBookerId nextBooking,
                                                              List<CommentDto> comments);

}
