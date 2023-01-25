package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDtoOutput {
    private Long id;
    private String description;
    private UserDtoShort requestor;
    private LocalDateTime created;
    private List<ItemDtoForRequest> items;
}
