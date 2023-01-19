package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingDtoConverterImpl implements BookingDtoConverter {


    @Override
    public BookingDtoOutput toOutputDto(Booking booking) {
        return new BookingDtoOutput(booking.getId(), booking.getStart(), booking.getEnd(),
                new ItemDtoShort(booking.getItem().getId(), booking.getItem().getName()),
                new UserDtoShort(booking.getBooker().getId(), booking.getBooker().getName()),
                booking.getStatus());
    }

    @Override
    public Booking fromInputDto(BookingDtoInput bookingDtoInput, Item item, User user) {
        return new Booking(null, bookingDtoInput.getStart(), bookingDtoInput.getEnd(),
                item, user, Status.WAITING);
    }

    @Override
    public BookingDtoOnlyIdAndBookerId toDtoOnlyIdAndBookerId(Booking booking) {
        return new BookingDtoOnlyIdAndBookerId(booking.getId(), booking.getBooker().getId());
    }
}
