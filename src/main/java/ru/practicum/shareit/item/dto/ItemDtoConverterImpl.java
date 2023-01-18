package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemDtoConverterImpl implements ItemDtoConverter {
    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwner(), item.getRequest());
    }

    @Override
    public Item fromDto(long sharerId, ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(), itemDto.getOwner(), itemDto.getRequest());
    }

    @Override
    public ItemDtoWithBookingAndComments toDtoWithBookingAndComments(Item item,
                                                                     BookingDtoOnlyIdAndBookerId lastBooking,
                                                                     BookingDtoOnlyIdAndBookerId nextBooking,
                                                                     List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComments(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwner(), item.getRequest(),
                lastBooking, nextBooking, comments);
    }
}
