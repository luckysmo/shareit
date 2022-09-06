package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findBookingByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findBookingByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findBookingByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findBookingByItem_OwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findBookingByItem_OwnerId(Long ownerId);

    List<Booking> findBookingByItem_Id(Long itemId);

    List<Booking> findBookingByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    List<Booking> findBookingByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                      LocalDateTime time1,
                                                                      LocalDateTime time2);

    List<Booking> findBookingByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                         LocalDateTime time1,
                                                                         LocalDateTime time2);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);
}
