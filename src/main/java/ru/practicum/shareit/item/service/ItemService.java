package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(long sharerId);

    ItemDto getById(long id);

    List<ItemDto> getByText(String text);

    ItemDto create(long sharerId, ItemDto itemDto);

    ItemDto update(long sharerId, long id, ItemDto itemDto);

    void deleteById(long sharerId, long id);

    void deleteAll();
}
