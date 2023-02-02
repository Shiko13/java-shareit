package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookingAndComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOnlyIdAndBookerId lastBooking;
    private BookingDtoOnlyIdAndBookerId nextBooking;
    private List<CommentDto> comments;
}
