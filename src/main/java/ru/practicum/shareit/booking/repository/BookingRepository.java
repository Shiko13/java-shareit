package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBooker_Id(long bookerId, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfter(long bookerId,
                                                                   Sort sort, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByBooker_IdAndEndBefore(long bookerId, Sort sort, LocalDateTime end);

    List<Booking> findBookingsByBooker_IdAndStartAfter(long bookerId, Sort sort, LocalDateTime start);

    List<Booking> findBookingsByBooker_IdAndStatus(long bookerId, Sort sort, Status status);

    List<Booking> findBookingsByItem_Owner_Id(long ownerId, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStartBeforeAndEndAfter(long ownerId,
                                                                   Sort sort, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItem_Owner_IdAndEndBefore(long ownerId, Sort sort, LocalDateTime end);

    List<Booking> findBookingsByItem_Owner_IdAndStartAfter(long ownerId, Sort sort, LocalDateTime start);

    List<Booking> findBookingsByItem_Owner_IdAndStatus(long ownerId, Sort sort, Status status);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndEndIsBefore(long bookerId, long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItem_IdAndStartBeforeOrderByEndDesc(long id, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItem_IdAndStartAfterOrderByEndDesc(long id, LocalDateTime localDateTime);

    @Query(value = "select b from Booking as b where b.item.id in ?1 and b.status = 'APPROVED'" +
            " and b.start <= current_timestamp order by b.end desc")
    List<Booking> findByItem_IdInAndStartBeforeOrderByEndDesc(Set<Long> itemsId);

    @Query(value = "select b from Booking as b where b.item.id in ?1 and b.status = 'APPROVED'" +
            " and b.start > current_timestamp order by b.end asc")
    List<Booking> findByItem_IdInAndStartAfterOrderByEndAsc(Set<Long> itemsId);
}
