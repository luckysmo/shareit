package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoForCreated {
    private final long id;
    private final long itemId;
    @FutureOrPresent
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;
}
