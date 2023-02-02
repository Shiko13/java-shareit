package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestClient.getItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAnotherUsers(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return itemRequestClient.getAllAnotherUsers(requestorId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(requestorId, itemRequestDto);
    }
}
