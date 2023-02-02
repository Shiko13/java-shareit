package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput create(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestBody BookingDtoInput bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput updateStatus(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                         @PathVariable  long bookingId,
                                         @RequestParam boolean approved) {

        return bookingService.updateStatus(sharerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam State state,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam State state,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}
