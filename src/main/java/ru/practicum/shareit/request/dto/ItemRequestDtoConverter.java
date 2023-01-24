package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestDtoConverter {

    public static ItemRequestDtoOutput toDtoOutput(ItemRequest itemRequest, List<ItemDtoForRequest> items) {
        return new ItemRequestDtoOutput(itemRequest.getId(), itemRequest.getDescription(),
                new UserDtoShort(itemRequest.getRequestor().getId(),
                        itemRequest.getRequestor().getName()),
                itemRequest.getCreated(), items);
    }

    public static ItemRequest fromDtoInput(ItemRequestDtoInput itemRequest, User owner) {
        return new ItemRequest(itemRequest.getId(), itemRequest.getDescription(),
                owner, itemRequest.getCreated() == null ? LocalDateTime.now() : itemRequest.getCreated());
    }
}
