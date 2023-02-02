package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoConverter;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoOutput create(long userId, BookingDtoInput bookingDto) {
        log.debug("Start request POST to /bookings");

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + bookingDto.getItemId() + " not found"));

        Booking booking = BookingDtoConverter.fromInputDto(bookingDto, item, user);
        if (booking.getItem().getOwner().getId() == userId) {
            throw new AccessException("Owner can't booking item");
        }
        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }

        booking.setStatus(Status.WAITING);

        Booking newBooking = bookingRepository.save(booking);
        return BookingDtoConverter.toOutputDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDtoOutput updateStatus(long sharerId, long id, boolean approved) {
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

        return BookingDtoConverter.toOutputDto(booking);
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
            return BookingDtoConverter.toOutputDto(booking);
        } else {
            throw new AccessException("You are not owner or booker of this item");
        }
    }

    @Override
    public List<BookingDtoOutput> getAllByUser(long userId, State state, int from, int size) {
        log.debug("Start request GET to /bookings");
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with this id not found")
        );

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByBooker_Id(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBooker_IdAndEndBefore(userId,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartAfter(userId,
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId,
                        Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId,
                        Status.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException();
        }
        return bookings.stream().map(BookingDtoConverter::toOutputDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOutput> getAllByOwner(long ownerId, State state, int from, int size) {
        log.debug("Start request GET to /bookings/owner");
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("User with this id not found")
        );

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByItem_Owner_Id(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndBefore(ownerId,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartAfter(ownerId,
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId,
                        Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId,
                        Status.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException();
        }
        return bookings.stream().map(BookingDtoConverter::toOutputDto).collect(Collectors.toList());
    }
}