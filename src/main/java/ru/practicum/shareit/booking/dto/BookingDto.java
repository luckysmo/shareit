package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final UserDto booker;
    private final BookingStatus status;
    private final Item item;
}
