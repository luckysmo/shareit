package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoForCreated;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    BookingDtoWithTime bookingDto = new BookingDtoWithTime(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            BookingStatus.APPROVED,
            new UserDto(1L, "user", "email@mail.ru"),
            new ItemDtoForCreate(),
            "item");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateBooking() throws Exception {
        BookingDtoForCreated bookingDtoForCreated = new BookingDtoForCreated(
                1,
                1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        when(service.createBooking(1L, bookingDtoForCreated))
                .thenReturn(bookingDtoForCreated);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoForCreated))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForCreated.getId()), Long.class));
    }

    @Test
    void whenCreateBookingPastStart_thenReturnedStatus400() throws Exception {
        BookingDtoForCreated bookingDtoForCreated = new BookingDtoForCreated(
                1,
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2));

        when(service.createBooking(1L, bookingDtoForCreated))
                .thenReturn(bookingDtoForCreated);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoForCreated))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenCreateBookingPastEnd_thenReturnStatus400() throws Exception {
        BookingDtoForCreated bookingDtoForCreated = new BookingDtoForCreated(
                1,
                1,
                LocalDateTime.now(),
                LocalDateTime.now().minusHours(2));

        when(service.createBooking(1L, bookingDtoForCreated))
                .thenReturn(bookingDtoForCreated);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoForCreated))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testApprovedBooking() throws Exception {

        when(service.approved(1L, 1L, true))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingDto.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus()
                        .toString())));
    }

    @Test
    void testGetById() throws Exception {
        when(service.getById(1L, 1L))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus()
                        .toString())));
    }

    @Test
    void testGetBookingOfCurrentUser() throws Exception {
        List<BookingDtoWithTime> bookings = List.of(bookingDto);

        when(service.getBookingCurrentUser(State.ALL, 1L, 0, 20))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }

    @Test
    void testGetAllForOwner() throws Exception {
        List<BookingDtoWithTime> bookings = List.of(bookingDto);

        when(service.getBookingByOwner(State.ALL, 1L, 0, 20))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()));
    }
}
