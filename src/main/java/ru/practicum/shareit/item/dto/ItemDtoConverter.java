package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoConverter;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemDtoConverter {

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                UserDtoConverter.toDto(item.getOwner()),
                item.getRequest() == null ? null :
                item.getRequest().getId());
    }


    public static Item fromDtoInput(ItemDtoInput itemDto, User owner, ItemRequest itemRequest) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, itemRequest);
    }

    public static List<ItemDtoForRequest> toDtoListForRequest(List<Item> items) {
        return items.stream()
                .map(ItemDtoConverter::toDtoForRequest)
                .collect(Collectors.toList());
    }

    public static ItemDtoForRequest toDtoForRequest(Item item) {
        return new ItemDtoForRequest(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequest().getId());
    }


    public static ItemDtoWithBookingAndComments toDtoWithBookingAndComments(Item item,
                                                                     BookingDtoOnlyIdAndBookerId lastBooking,
                                                                     BookingDtoOnlyIdAndBookerId nextBooking,
                                                                     List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComments(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                lastBooking, nextBooking, comments);
    }
}
