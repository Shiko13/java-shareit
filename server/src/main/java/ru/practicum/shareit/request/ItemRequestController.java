package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDtoOutput> getAll(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestService.getAll(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAllAnotherUsers(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemRequestService.getAllAnotherUsers(requestorId, from, size);
    }

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                             @RequestBody ItemRequestDtoInput itemRequestDtoInput) {
        return itemRequestService.create(requestorId, itemRequestDtoInput);
    }
}
