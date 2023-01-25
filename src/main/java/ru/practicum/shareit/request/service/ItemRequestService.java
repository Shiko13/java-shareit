package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDtoOutput> getAll(long requestorId);

    ItemRequestDtoOutput getById(long userId, long requestId);

    List<ItemRequestDtoOutput> getAllAnotherUsers(long requestorId, int from, int size);

    ItemRequestDtoOutput create(long requestorId, ItemRequestDtoInput itemRequest);
}
