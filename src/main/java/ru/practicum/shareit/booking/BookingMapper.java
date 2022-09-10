package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreated;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.item.ItemMapper.mapToItemDtoForCreate;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus()
        );
    }

    public static Booking mapToBooking(BookingDtoForCreated bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                null
        );
    }

    public static BookingDtoWithTime mapToBookingDtoWithTime(Booking booking) {
        Item item = booking.getItem();
        return new BookingDtoWithTime(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                mapToUserDto(booking.getBooker()),
                mapToItemDtoForCreate(item),
                item.getName()
        );
    }

    public static BookingDtoForCreated mapToBookingDtoForCreated(Booking booking) {
        return new BookingDtoForCreated(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
