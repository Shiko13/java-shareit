package ru.practicum.shareit.item.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDtoInput {
    private Long id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
