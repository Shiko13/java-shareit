package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(long id);

    List<Item> findAll(long sharerId);

    List<Item> findByText(String text);

    Item save(Item item);

    Item update(Item item);

    void deleteById(long id);

    void deleteAll();
}
