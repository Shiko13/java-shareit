package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long sharerId) {
        return itemService.getAll(sharerId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable long id) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.getByText(text);
        }
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long sharerId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(sharerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") long sharerId,
                         @PathVariable long id, @RequestBody ItemDto itemDto) {
        return itemService.update(sharerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestHeader("X-Sharer-User-Id") long sharerId,
                           @PathVariable long id) {
        itemService.deleteById(sharerId, id);
    }

    @DeleteMapping
    public void deleteAll() {
        itemService.deleteAll();
    }
}
