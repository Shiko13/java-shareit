package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface BookingDtoConverter {

    BookingDtoOutput toOutputDto(Booking booking);

    Booking fromInputDto(BookingDtoInput bookingDtoInput, Item item, User user);

    BookingDtoOnlyIdAndBookerId toDtoOnlyIdAndBookerId(Booking booking);
}
