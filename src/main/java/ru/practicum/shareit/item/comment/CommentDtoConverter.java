package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentDtoConverter {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment fromDto(CommentDto commentDto, Item item, User user) {
        return new Comment(commentDto.getId(), commentDto.getText(),
                item, user, LocalDateTime.now());
    }
}
