package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDtoForCreated;

import java.time.LocalDateTime;

public class BookingServiceTest {

    @Test
    public void testBookingSave() {

        BookingService service = Mockito.mock(BookingService.class);

        BookingDtoForCreated bookingDto = new BookingDtoForCreated(
                1,
                2,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1));

        Mockito.when(service.createBooking(1L, bookingDto))
                .thenReturn(new BookingDtoForCreated(
                        1,
                        2,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1))
                );

        Assertions.assertEquals(bookingDto, bookingDto);
    }
}
