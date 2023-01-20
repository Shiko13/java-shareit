package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoConverter;
import ru.practicum.shareit.user.dto.UserDtoConverter;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemDtoConverterImpl implements ItemDtoConverter {

    private final UserDtoConverter userDtoConverter;
    private final ItemRequestDtoConverter itemRequestDtoConverter;

    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                userDtoConverter.toDto(item.getOwner()),
                item.getRequest() == null ? null :
                itemRequestDtoConverter.toDto(item.getRequest()));
    }

    @Override
    public Item fromDto(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, itemDto.getRequest() == null ?
                null : itemRequestDtoConverter.fromDto(itemDto.getRequest()));
    }

    @Override
    public Item fromDtoInput(ItemDtoInput itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, null);
    }

    @Override
    public ItemDtoWithBookingAndComments toDtoWithBookingAndComments(Item item,
                                                                     BookingDtoOnlyIdAndBookerId lastBooking,
                                                                     BookingDtoOnlyIdAndBookerId nextBooking,
                                                                     List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComments(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                lastBooking, nextBooking, comments);
    }
}
