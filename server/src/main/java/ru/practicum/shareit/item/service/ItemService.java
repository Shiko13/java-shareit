package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBookingAndComments> getAll(long sharerId, int from, int size);

    ItemDtoWithBookingAndComments getById(long sharerId, long id);

    List<ItemDto> getByText(String text, int from, int size);

    ItemDto create(long sharerId, ItemDtoInput itemDto);

    ItemDto update(long sharerId, long id, ItemDto itemDto);

    void deleteById(long sharerId, long id);

    void deleteAll();

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
