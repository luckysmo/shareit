package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findBookingByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findBookingByItem_OwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    Page<Booking> findBookingByItem_OwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findBookingByItem_OwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findBookingByItem_OwnerId(Long ownerId);

    List<Booking> findBookingByItem_Id(Long itemId);

    List<Booking> findBookingByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    Page<Booking> findBookingByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                      LocalDateTime time1,
                                                                      LocalDateTime time2,
                                                                      Pageable pageable);

    Page<Booking> findBookingByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                         LocalDateTime time1,
                                                                         LocalDateTime time2,
                                                                         Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItem_OwnerIdAndEndIsBefore(Long userId, LocalDateTime time, Pageable pageable);
}
