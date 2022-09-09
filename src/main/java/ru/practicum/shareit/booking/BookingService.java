package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.mapToBooking;
import static ru.practicum.shareit.booking.BookingMapper.mapToBookingDtoWithTime;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.enums.BookingStatus.WAITING;
import static ru.practicum.shareit.booking.enums.State.ALL;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDtoWithTime createBooking(Long bookerId, BookingDto bookingDto) {
        Item itemBooking = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        if (bookerId.equals(itemBooking.getOwnerId())) {
            throw new NotFoundException("Owner can't booking his item!!!");
        }
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User not found!!!");
        }
        if (itemBooking.getAvailable()) {
            if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
                throw new ValidationException("The time is in the past!!!");
            }
            if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
                throw new ValidationException("The time is in the past!!!");
            }
            if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
                throw new ValidationException("Start is after end!!!");
            }
            User booker = userRepository.findById(bookerId)
                    .orElseThrow(() -> new NotFoundException("User not found!!!"));
            Booking booking = mapToBooking(bookingDto, itemBooking, booker);
            booking.setStatus(WAITING);
            return mapToBookingDtoWithTime(bookingRepository.save(booking));
        } else {
            throw new ValidationException("Item unavailable!!!");
        }
    }

    @Transactional
    public BookingDtoWithTime approved(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found!!!"));
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found!!!"));
        Item item = booking.getItem();
        if (booking.getStatus() == APPROVED && approved) {
            throw new ValidationException("Can't change status!");
        }
        if (item.getOwnerId().equals(owner.getId())) {
            if (approved) {
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
            itemRepository.save(item);
            return mapToBookingDtoWithTime(booking);
        } else {
            throw new NotFoundException("Not owner can't be updating booking!");
        }
    }

    @Transactional(readOnly = true)
    public BookingDtoWithTime getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("User not found!!!"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (booker.getId().equals(userId) || item.getOwnerId().equals(userId)) {
            return mapToBookingDtoWithTime(booking);
        } else {
            throw new NotFoundException("User don't have item or booking!!!");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingDtoWithTime> getAllForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return extractedFromListBookingsAndMapToListBookingDto(
                bookingRepository.findBookingByBookerIdOrderByStartDesc(userId));
    }

    @Transactional(readOnly = true)
    public List<BookingDtoWithTime> getAllForOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found");
        }
        return extractedFromListBookingsAndMapToListBookingDto(
                bookingRepository.findBookingByItem_OwnerIdOrderByStartDesc(ownerId));

    }

    @Transactional(readOnly = true)
    public List<BookingDtoWithTime> getBookingCurrentUser(State state, Long userId) {
        List<BookingDtoWithTime> result = new ArrayList<>();
        if (state == null) {
            state = ALL;
        }
        switch (state) {
            case ALL:
                result = getAllForUser(userId);
                break;
            case PAST:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()));
                break;
            case WAITING:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, WAITING));
                break;
            case REJECTED:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, REJECTED));
                break;
            case FUTURE:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()));
                break;
            case CURRENT:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                userId,
                                LocalDateTime.now(),
                                LocalDateTime.now()));
                break;
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<BookingDtoWithTime> getBookingByOwner(State state, Long ownerId) {
        List<BookingDtoWithTime> result = new ArrayList<>();
        if (state == null) {
            state = ALL;
        }
        switch (state) {
            case ALL:
                result = getAllForOwner(ownerId);
                break;
            case PAST:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(
                                ownerId,
                                LocalDateTime.now()));
                break;
            case WAITING:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, WAITING));
                break;
            case REJECTED:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, REJECTED));
                break;
            case FUTURE:
                LocalDateTime time = LocalDateTime.now();
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStartIsAfterOrderByStartDesc(ownerId, time));
                break;
            case CURRENT:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(
                                ownerId,
                                LocalDateTime.now(),
                                LocalDateTime.now()));
                break;
        }
        return result;
    }

    private List<BookingDtoWithTime> extractedFromListBookingsAndMapToListBookingDto(List<Booking> bookings) {
        List<BookingDtoWithTime> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(mapToBookingDtoWithTime(booking));
        }
        return result;
    }
}
