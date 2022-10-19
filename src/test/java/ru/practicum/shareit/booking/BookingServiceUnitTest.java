package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoForCreated;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingService bookingService;

    BookingDtoForCreated bookingDtoForCreated = new BookingDtoForCreated(
            1L,
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2)
    );

    @Test
    void whenCreateBooking_thenCallBookingRepository() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item, booker, BookingStatus.APPROVED);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.createBooking(1L, bookingDtoForCreated);

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void whenBookerIsOwner_thenTrowNotFoundException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, new BookingDtoForCreated(
                        1,
                        1,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2))));

        Assertions.assertEquals("Owner can't booking his item!!!", notFoundException.getMessage());
    }

    @Test
    void whenBookingAvailableIsFalse_thenTrowValidationException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", false, owner, itemRequest);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, new BookingDtoForCreated(
                        1,
                        1,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2))));

        Assertions.assertEquals("Item unavailable!!!", validationException.getMessage());
    }

    @Test
    void whenBookingStartIsAfterEnd_thenTrowValidationException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, new BookingDtoForCreated(
                        1,
                        1,
                        LocalDateTime.now().plusHours(2),
                        LocalDateTime.now().plusHours(1))));

        Assertions.assertEquals("Start is after end!!!", validationException.getMessage());
    }

    @Test
    void whenBookingStartEqualEnd_thenTrowValidationException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, new BookingDtoForCreated(
                        1,
                        1,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(1))));

        Assertions.assertEquals("Start cannot be equal to end!!!", validationException.getMessage());
    }

    @Test
    void whenStatusApproved_ThenTrowValidationException() {
        User user = new User(1L, "user", "user@mail.com");
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                new Item(), user, BookingStatus.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.approved(1L, 1L, true));

        Assertions.assertEquals("Can't change status!", validationException.getMessage());
    }

    @Test
    void whenNotOwnerChangeStatus_thenThrowNotFoundException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item, booker, BookingStatus.WAITING);


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approved(1L, 1L, true));

        Assertions.assertEquals("Not owner can't be updating booking!", notFoundException.getMessage());
    }

    @Test
    void whenApprovedIsFalse_thenChangeStatusREJECTED() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoWithTime approved = bookingService.approved(1L, 1L, false);

        Assertions.assertEquals(BookingStatus.REJECTED, approved.getStatus());
    }

    @Test
    void whenApprovedIsTrue_thenChangeStatusAPPROVED() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoWithTime approved = bookingService.approved(1L, 1L, true);

        Assertions.assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void whenBookingGetByIdCallNotBooker_thenTrowNotFoundException() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getById(1L, 3L));

        Assertions.assertEquals("User don't have item or booking!!!", notFoundException.getMessage());
    }

    @Test
    void whenBookingGetByIdBooker_thenReturnBooking() {
        User booker = new User(1L, "user", "user@mail.com");
        User owner = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
        Item item = new Item(1L, "item", "des", true, owner, itemRequest);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoWithTime byId = bookingService.getById(1L, 1L);

        Assertions.assertEquals(BookingStatus.WAITING, byId.getStatus());
    }

    @Test
    void whenGetAllWithNonExistentUser_thenTrowNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllForUser(100L, 0, 20));

        Assertions.assertEquals("User not found", notFoundException.getMessage());
    }

    @Test
    void whenGetAllForCurrentUserWithNonExistentUser_thenTrowNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllForOwner(100L, 0, 20));

        Assertions.assertEquals("User not found", notFoundException.getMessage());
    }
}