package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface CommentDtoConverter {
    CommentDto toDto(Comment comment);

    Comment fromDto(CommentDto commentDto, Item item, User user);
}
