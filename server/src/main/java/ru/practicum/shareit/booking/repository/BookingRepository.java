package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBooker_Id(long bookerId, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start,
                                                                   LocalDateTime end,
                                                                    Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndEndBefore(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStartAfter(long bookerId, LocalDateTime start,
                                                       Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findBookingsByItem_Owner_Id(long ownerId, Pageable pageable);

    List<Booking> findBookingsByItem_Owner_IdAndStartBeforeAndEndAfter(long ownerId,
                                                                       LocalDateTime start, LocalDateTime end,
                                                                       Pageable pageable);

    List<Booking> findBookingsByItem_Owner_IdAndEndBefore(long ownerId, LocalDateTime end,
                                                          Pageable pageable);

    List<Booking> findBookingsByItem_Owner_IdAndStartAfter(long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findBookingsByItem_Owner_IdAndStatus(long ownerId, Status status, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndEndIsLessThanEqual(long bookerId, long itemId,
                                                                         LocalDateTime localDateTime);

    Optional<Booking> findFirstByItem_IdAndStartIsLessThanEqualOrderByEndAsc(long id, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItem_IdAndStartAfterOrderByEndAsc(long id, LocalDateTime localDateTime);

    @Query(value = "select b from Booking as b where b.item.id in ?1 and b.status = 'APPROVED'" +
            " and b.start <= ?2 order by b.end desc")
    List<Booking> findByItem_IdInAndStartIsLessThanEqualOrderByEndDesc(Set<Long> itemsId, LocalDateTime date);

    @Query(value = "select b from Booking as b where b.item.id in ?1 and b.status = 'APPROVED'" +
            " and b.start > ?2 order by b.end asc")
    List<Booking> findByItem_IdInAndStartAfterOrderByEndAsc(Set<Long> itemsId, LocalDateTime date);
}
