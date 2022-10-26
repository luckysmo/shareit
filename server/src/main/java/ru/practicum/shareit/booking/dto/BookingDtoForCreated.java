package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoForCreated {
    private final long id;
    private final long itemId;
    private final LocalDateTime start;
    private final LocalDateTime end;
}
