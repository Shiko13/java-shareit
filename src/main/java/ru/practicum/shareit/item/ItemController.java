package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ItemValidated;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long sharerId) {
        return itemService.getAll(sharerId);
    }

    @GetMapping("/{id}")
    public ItemDto findItemById(@PathVariable long id) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestParam String text) {
        return itemService.getByText(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long sharerId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(sharerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long sharerId,
                             @PathVariable long id, @Validated(ItemValidated.class) @RequestBody ItemDto itemDto) {
        return itemService.update(sharerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable long id) {
        itemService.deleteById(id);
    }

    @DeleteMapping
    public void deleteAllItems() {
        itemService.deleteAll();
    }
}
