package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBookingAndComments> getAll(long sharerId);

    ItemDtoWithBookingAndComments getById(long sharerId, long id);

    List<ItemDto> getByText(String text);

    ItemDto create(long sharerId, ItemDto itemDto);

    ItemDto update(long sharerId, long id, ItemDto itemDto);

    void deleteById(long sharerId, long id);

    void deleteAll();

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
