package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.item.ItemMapper.mapToItemDtoForCreate;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

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
}
