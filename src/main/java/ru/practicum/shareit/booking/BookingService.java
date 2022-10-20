package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForCreated;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.mapToBooking;
import static ru.practicum.shareit.booking.BookingMapper.mapToBookingDtoForCreated;
import static ru.practicum.shareit.booking.BookingMapper.mapToBookingDtoWithTime;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.enums.BookingStatus.WAITING;

@Transactional(readOnly = true)
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
    public BookingDtoForCreated createBooking(Long bookerId, BookingDtoForCreated bookingDto) {
        Item itemBooking = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found!!!"));
        if (bookerId.equals(itemBooking.getOwner().getId())) {
            throw new NotFoundException("Owner can't booking his item!!!");
        }
        if (itemBooking.getAvailable()) {
            if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
                throw new ValidationException("Start is after end!!!");
            }
            if (bookingDto.getStart().equals(bookingDto.getEnd())) {
                throw new ValidationException("Start cannot be equal to end!!!");
            }
            Booking booking = mapToBooking(bookingDto, itemBooking, booker);
            booking.setStatus(WAITING);
            return mapToBookingDtoForCreated(bookingRepository.save(booking));
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
        if (item.getOwner().getId().equals(owner.getId())) {
            if (approved) {
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
            return mapToBookingDtoWithTime(booking);
        } else {
            throw new NotFoundException("Not owner can't be updating booking!");
        }
    }

    public BookingDtoWithTime getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found!!!"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (booker.getId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return mapToBookingDtoWithTime(booking);
        } else {
            throw new NotFoundException("User don't have item or booking!!!");
        }
    }

    public List<BookingDtoWithTime> getAllForUser(Long userId, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                .descending());
        if (userRepository.existsById(userId)) {
            return extractedFromListBookingsAndMapToListBookingDto(bookingRepository.findBookingByBookerId(
                    userId,
                    pageable).toList());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<BookingDtoWithTime> getAllForOwner(Long ownerId, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                .descending());
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found");
        }
        return extractedFromListBookingsAndMapToListBookingDto(bookingRepository.findBookingByItem_OwnerId(
                ownerId,
                pageable).toList());

    }

    public List<BookingDtoWithTime> getBookingCurrentUser(State state, Long userId, Integer from, Integer size) {
        List<BookingDtoWithTime> result = new ArrayList<>();

        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                .descending());

        switch (state) {
            case ALL:
                result = getAllForUser(userId, from, size);
                break;
            case PAST:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByBookerIdAndEndIsBefore(
                                userId,
                                LocalDateTime.now(),
                                pageable).toList());
                break;
            case WAITING:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBookerIdAndStatus(
                                userId,
                                WAITING,
                                pageable).toList());
                break;
            case REJECTED:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBookerIdAndStatus(
                                userId,
                                REJECTED,
                                pageable).toList());
                break;
            case FUTURE:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByBookerIdAndStartIsAfter(
                                userId,
                                LocalDateTime.now(),
                                pageable).toList());
                break;
            case CURRENT:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                userId,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageable).toList());
                break;
        }
        return result;
    }

    public List<BookingDtoWithTime> getBookingByOwner(State state,
                                                      Long ownerId,
                                                      @PositiveOrZero Integer from,
                                                      @Positive Integer size) {
        List<BookingDtoWithTime> result = new ArrayList<>();

        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                .descending());

        switch (state) {
            case ALL:
                result = getAllForOwner(ownerId, from, size);
                break;
            case PAST:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findByItem_OwnerIdAndEndIsBefore(
                                ownerId,
                                LocalDateTime.now(),
                                pageable).toList());
                break;
            case WAITING:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStatus(
                                ownerId,
                                WAITING,
                                pageable).toList());
                break;
            case REJECTED:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStatus(
                                ownerId,
                                REJECTED,
                                pageable).toList());
                break;
            case FUTURE:
                LocalDateTime time = LocalDateTime.now();
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStartIsAfter(
                                ownerId,
                                time,
                                pageable).toList());
                break;
            case CURRENT:
                result = extractedFromListBookingsAndMapToListBookingDto(
                        bookingRepository.findBookingByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(
                                ownerId,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageable).toList());
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
