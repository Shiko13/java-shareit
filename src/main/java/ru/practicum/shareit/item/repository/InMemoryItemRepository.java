package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Item>> sharersWithItems = new HashMap<>();
    private long count = 1;

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll(long sharerId) {
        return new ArrayList<>(sharersWithItems.get(sharerId));
    }

    @Override
    public List<Item> findByText(String text) {
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        (i.getDescription().toLowerCase().contains(text.toLowerCase()))
                                && i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        item.setId(count++);
        items.put(item.getId(), item);
        final Set<Item> itemSet =
                sharersWithItems.computeIfAbsent(item.getSharerId(), k -> new HashSet<>());
        itemSet.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return item;
    }

    @Override
    public void deleteById(long sharerId, long id) {
        sharersWithItems.get(sharerId).remove(items.remove(id));
    }

    @Override
    public void deleteAll() {
        items.clear();
        sharersWithItems.clear();
    }
}
