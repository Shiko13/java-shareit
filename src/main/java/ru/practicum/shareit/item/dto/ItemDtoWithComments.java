package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
}
