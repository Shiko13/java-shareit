package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoConverter;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.item.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoConverter bookingDtoConverter;

    @Override
    public BookingDtoOutput create(long userId, BookingDtoInput bookingDto) {
        log.debug("Start request POST to /bookings");
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new TimeException("You are not in Nolan movie :)");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + bookingDto.getItemId() + " not found"));

        Booking booking = bookingDtoConverter.fromInputDto(bookingDto, item, user);
        if (booking.getItem().getOwner().getId() == userId) {
            throw new AccessException("Owner can't booking item");
        }
        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }

        booking.setStatus(Status.WAITING);

        Booking newBooking = bookingRepository.save(booking);
        return bookingDtoConverter.toOutputDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDtoOutput updateStatusOfBooking(long sharerId, long id, boolean approved) {
        log.debug("Start request PATCH to /bookings/{}", id);
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Booking with this id not found")
        );
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));

        if (booking.getItem().getOwner().getId() != sharerId) {
            throw new AccessException("You are not owner of this item");
        }

        Status status;
        if (approved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        if (booking.getStatus() == status) {
            throw new IllegalArgumentException("Same status");
        }
        booking.setStatus(status);

        Booking newBooking = bookingRepository.save(booking);
        return bookingDtoConverter.toOutputDto(newBooking);
    }

    @Override
    public BookingDtoOutput getById(long userId, long id) {
        log.debug("Start request GET to /bookings/{}", id);
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Booking with this id not found")
        );
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingDtoConverter.toOutputDto(booking);
        } else {
            throw new AccessException("You are not owner or booker of this item");
        }
    }

    @Override
    public List<BookingDtoOutput> getAllByUser(long userId, State state) {
        log.debug("Start request GET to /bookings");
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with this id not found")
        );

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByBooker_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfter(userId,
                        sort, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBooker_IdAndEndBefore(userId,
                        sort, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartAfter(userId,
                        sort, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId,
                        sort, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId,
                        sort, Status.REJECTED);
                break;
            default:
                throw new UnknownStateException();
        }
        return bookings.stream().map(bookingDtoConverter::toOutputDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOutput> getAllByOwner(long ownerId, State state) {
        log.debug("Start request GET to /bookings/owner");
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("User with this id not found")
        );

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByItem_Owner_Id(ownerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId,
                        sort, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndBefore(ownerId,
                        sort, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartAfter(ownerId,
                        sort, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId,
                        sort, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId,
                        sort, Status.REJECTED);
                break;
            default:
                throw new UnknownStateException();
        }
        return bookings.stream().map(bookingDtoConverter::toOutputDto).collect(Collectors.toList());
    }
}