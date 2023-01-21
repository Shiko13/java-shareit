package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.util.List;

public interface BookingService {
    BookingDtoOutput create(long userId, BookingDtoInput bookingDto);

    BookingDtoOutput updateStatusOfBooking(long sharerId, long id, boolean approved);

    BookingDtoOutput getById(long userId, long id);

    List<BookingDtoOutput> getAllByUser(long userId, State state);

    List<BookingDtoOutput> getAllByOwner(long ownerId, State state);
}
