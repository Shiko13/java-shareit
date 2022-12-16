package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long sharerId;
    private ItemRequest request;

    public Item(long id, String name, String description, Boolean available, long sharerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.sharerId = sharerId;
    }
}
