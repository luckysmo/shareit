package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByBookerId(Long bookerId, Sort sort);

    List<Booking> findBookingByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItem_OwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItem_OwnerId(Long ownerId, Sort sort);

    List<Booking> findBookingByItem_OwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItem_OwnerId(Long ownerId);

    List<Booking> findBookingByItem_Id(Long itemId);

    List<Booking> findBookingByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    List<Booking> findBookingByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                      LocalDateTime time1,
                                                                      LocalDateTime time2);

    List<Booking> findBookingByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                         LocalDateTime time1,
                                                                         LocalDateTime time2);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_OwnerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);
}
