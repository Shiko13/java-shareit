package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

import static org.springframework.http.RequestEntity.delete;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllBy(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "100") @Positive int size) {
        return itemClient.getItems(sharerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                                 @PathVariable long itemId) {
        log.info("Get sharerId {}, itemId={}", sharerId, itemId);
        return itemClient.getItem(sharerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestParam String text,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(defaultValue = "100") @Positive int size) {

            if (text.isBlank()) {
                return new ResponseEntity<>(List.of(), HttpStatus.OK);
            } else {
                return itemClient.getItemByText(text, from, size);
            }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long sharerId,
                          @Valid @RequestBody ItemDto itemDto) {

        return itemClient.createItem(sharerId, itemDto);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long sharerId,
                          @PathVariable long id, @RequestBody ItemDto itemDto) {
        return itemClient.patchItem(sharerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestHeader("X-Sharer-User-Id") long sharerId,
                           @PathVariable long id) {
        delete("/" + id, sharerId);
    }

    @DeleteMapping
    public void deleteAll() {
        delete("");
    }
}
