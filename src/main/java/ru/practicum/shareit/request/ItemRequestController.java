package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return itemRequestService.getAllAnotherUsers(requestorId, from, size);
    }

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                             @RequestBody @Valid ItemRequestDtoInput itemRequestDtoInput) {
        return itemRequestService.create(requestorId, itemRequestDtoInput);
    }
}
