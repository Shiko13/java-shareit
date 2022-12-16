package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> sharersWithItems = new HashMap<>();
    private long count = 1;

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll(long sharerId) {
        Set<Long> itemsOfSharer = sharersWithItems.get(sharerId);
        List<Item> itemsList = new ArrayList<>();
        for (Long id : items.keySet()) {
            if (itemsOfSharer.contains(id)) {
                itemsList.add(items.get(id));
            }
        }
        return itemsList;
    }

    @Override
    public List<Item> findByText(String text) {
        List<Item> filterByTestItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    && item.getAvailable()) {
                filterByTestItems.add(item);
            }
        }
        return filterByTestItems;
    }

    @Override
    public Item save(Item item) {
        item.setId(count++);
        items.put(item.getId(), item);
        if (sharersWithItems.containsKey(item.getSharerId())) {
            sharersWithItems.get(item.getSharerId()).add(item.getId());
        } else {
            sharersWithItems.put(item.getSharerId(), new HashSet<>());
            sharersWithItems.get(item.getSharerId()).add(item.getId());
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!sharersWithItems.containsKey(item.getSharerId())
                || !sharersWithItems.get(item.getSharerId()).contains(item.getId())) {
            throw new NotFoundException("Item with this id not found in this user");
        }
        if (items.containsKey(item.getId())) {
            items.replace(item.getId(), item);
        } else {
            throw new NotFoundException("Item with this id not found");
        }
        return item;
    }

    @Override
    public void deleteById(long id) {
        items.remove(id);
    }

    @Override
    public void deleteAll() {
        items.clear();
    }
}
