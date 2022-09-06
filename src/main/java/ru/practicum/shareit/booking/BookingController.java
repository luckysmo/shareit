package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoWithTime createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDtoWithTime updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam boolean approved) {
        return bookingService.approved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithTime getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoWithTime> getBookingCurrentUser(@RequestParam(required = false) State state,
                                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingCurrentUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoWithTime> getAllForOwner(@RequestParam(required = false) State state,
                                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByOwner(state, userId);
    }
}
