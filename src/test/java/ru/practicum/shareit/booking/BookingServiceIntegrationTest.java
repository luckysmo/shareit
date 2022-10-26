package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.booking.enums.State.ALL;

@Transactional
@SpringBootTest(
        properties = "spring.data.mongodb.database= testdb",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService service;
    private final EntityManager entityManager;

    @Test
    public void whenGetByIdExist_thenReturnBookingWithStatusDto() {
        User booker = new User(null, "user", "user@mail.com");
        User owner = new User(null, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));

        entityManager.persist(booker);
        entityManager.persist(owner);
        entityManager.persist(itemRequest);

        Item item = new Item(null, "item", "des", true, owner, itemRequest);
        entityManager.persist(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);

        entityManager.persist(booking);

        BookingDtoWithTime receivedBooking = service.getById(booking.getId(), booker.getId());

        assertThat(receivedBooking.getId(), notNullValue());
        assertThat(booking.getId(), equalTo(receivedBooking.getId()));
        assertThat(booking.getItem().getId(), equalTo(receivedBooking.getItem().getId()));
        assertThat(booking.getStart(), equalTo(receivedBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(receivedBooking.getEnd()));
        assertThat(booker.getId(), equalTo(receivedBooking.getBooker().getId()));
    }

    @Test
    public void whenApprovedIsTrue_thenReturnBookingWithStatusAPPROVED() {
        User booker = new User(null, "user", "user@mail.com");
        User owner = new User(null, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));

        entityManager.persist(booker);
        entityManager.persist(owner);

        Item item = new Item(null, "item", "des", true, owner, itemRequest);
        entityManager.persist(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                null);

        entityManager.persist(booking);

        BookingDtoWithTime createdBooking = service.approved(owner.getId(), booking.getId(), true);

        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void whenApprovedIsFalse_thenReturnBookingWithStatusREJECTED() {
        User booker = new User(null, "user", "user@mail.com");
        User owner = new User(null, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));

        entityManager.persist(booker);
        entityManager.persist(owner);

        Item item = new Item(null, "item", "des", true, owner, itemRequest);
        entityManager.persist(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                null);

        entityManager.persist(booking);

        BookingDtoWithTime createdBooking = service.approved(owner.getId(), booking.getId(), false);

        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    public void whenApproveBooking_thenBookingWithApprovedStatus() {
        User booker = new User(null, "user", "user@mail.com");
        User owner = new User(null, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));

        entityManager.persist(booker);
        entityManager.persist(owner);

        Item item = new Item(null, "item", "des", true, owner, itemRequest);
        entityManager.persist(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                null);
        entityManager.persist(booking);

        BookingDtoWithTime approvedBooking = service.approved(owner.getId(), booking.getId(), true);

        assertThat(approvedBooking.getId(), notNullValue());
        assertThat(booking.getId(), equalTo(approvedBooking.getId()));
        assertThat(booking.getItem().getId(), equalTo(approvedBooking.getItem().getId()));
        assertThat(BookingStatus.APPROVED, equalTo(approvedBooking.getStatus()));
    }

    @Test
    public void whenGetAllForUser_thenReturnBookingsByBooker() {
        List<User> bookers = List.of(new User(null, "user1", "user1@mail.com"),
                new User(null, "user2", "user2@mail.com"));
        bookers.forEach(entityManager::persist);

        User owner = new User(null, "owner", "user3@mail.com");
        entityManager.persist(owner);

        User booker = new User(null, "booker", "user4@mail.com");
        entityManager.persist(booker);
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));
        entityManager.persist(itemRequest);
        Item item1 = new Item(null, "item1", "des", true, owner, itemRequest);
        Item item2 = new Item(null, "item2", "des", true, owner, itemRequest);
        Item item3 = new Item(null, "item3", "des", true, owner, itemRequest);
        List<Item> items = List.of(item1, item2, item3);
        items.forEach(entityManager::persist);

        Booking booking1 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1,
                booker,
                null);
        Booking booking2 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item2,
                booker,
                null);
        Booking booking3 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item3,
                booker,
                null);
        List<Booking> bookings = List.of(booking1, booking2, booking3);
        bookings.forEach(entityManager::persist);

        List<BookingDtoWithTime> bookerItems = service.getAllForUser(booker.getId(), 0, 20);

        assertThat(bookerItems, hasSize(bookings.size()));
        assertThat(bookerItems.get(0).getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    public void whenGetAllForOwner_thenReturnBookingsByOwner() {
        List<User> bookers = List.of(new User(null, "user1", "user1@mail.com"),
                new User(null, "user2", "user2@mail.com"));
        bookers.forEach(entityManager::persist);

        User owner = new User(null, "owner", "user3@mail.com");
        entityManager.persist(owner);

        User booker = new User(null, "booker", "user4@mail.com");
        entityManager.persist(booker);
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));
        entityManager.persist(itemRequest);
        Item item1 = new Item(null, "item1", "des", true, owner, itemRequest);
        Item item2 = new Item(null, "item2", "des", true, owner, itemRequest);
        Item item3 = new Item(null, "item3", "des", true, owner, itemRequest);
        List<Item> items = List.of(item1, item2, item3);
        items.forEach(entityManager::persist);

        Booking booking1 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1,
                booker,
                null);
        Booking booking2 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item2,
                booker,
                null);
        Booking booking3 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item3,
                booker,
                null);
        List<Booking> bookings = List.of(booking1, booking2, booking3);
        bookings.forEach(entityManager::persist);

        List<BookingDtoWithTime> ownerItems = service.getAllForOwner(owner.getId(), 0, 20);

        assertThat(ownerItems, hasSize(bookings.size()));
        assertThat(ownerItems.get(0).getId(), equalTo(bookings.get(0).getId()));
    }

    @Test
    public void whenGetBookingCurrentUser_thenReturnBookingsByBooker() {
        List<User> bookers = List.of(new User(null, "user1", "user1@mail.com"),
                new User(null, "user2", "user2@mail.com"));
        bookers.forEach(entityManager::persist);

        User owner = new User(null, "owner", "user3@mail.com");
        entityManager.persist(owner);

        User booker = new User(null, "booker", "user4@mail.com");
        entityManager.persist(booker);
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));
        entityManager.persist(itemRequest);
        Item item1 = new Item(null, "item1", "des", true, owner, itemRequest);
        Item item2 = new Item(null, "item2", "des", true, owner, itemRequest);
        Item item3 = new Item(null, "item3", "des", true, owner, itemRequest);
        List<Item> items = List.of(item1, item2, item3);
        items.forEach(entityManager::persist);

        Booking booking1 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1,
                booker,
                null);
        Booking booking2 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item2,
                booker,
                null);
        Booking booking3 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item3,
                booker,
                null);
        List<Booking> bookings = List.of(booking1, booking2, booking3);
        bookings.forEach(entityManager::persist);

        List<BookingDtoWithTime> bookerItems = service.getBookingCurrentUser(ALL, booker.getId(), 0, 20);

        assertThat(bookerItems, hasSize(bookings.size()));
        assertThat(bookerItems.get(0).getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    public void whenGetBookingByOwner_thenReturnBookingsByOwner() {
        List<User> bookers = List.of(new User(null, "user1", "user1@mail.com"),
                new User(null, "user2", "user2@mail.com"));
        bookers.forEach(entityManager::persist);

        User owner = new User(null, "owner", "user3@mail.com");
        entityManager.persist(owner);

        User booker = new User(null, "booker", "user4@mail.com");
        entityManager.persist(booker);
        ItemRequest itemRequest = new ItemRequest(null, "des", booker, LocalDateTime.now().plusHours(1));
        entityManager.persist(itemRequest);
        Item item1 = new Item(null, "item1", "des", true, owner, itemRequest);
        Item item2 = new Item(null, "item2", "des", true, owner, itemRequest);
        Item item3 = new Item(null, "item3", "des", true, owner, itemRequest);
        List<Item> items = List.of(item1, item2, item3);
        items.forEach(entityManager::persist);

        Booking booking1 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1,
                booker,
                null);
        Booking booking2 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item2,
                booker,
                null);
        Booking booking3 = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item3,
                booker,
                null);
        List<Booking> bookings = List.of(booking1, booking2, booking3);
        bookings.forEach(entityManager::persist);

        List<BookingDtoWithTime> ownerItems = service.getBookingByOwner(ALL, owner.getId(), 0, 20);

        assertThat(ownerItems, hasSize(bookings.size()));
        assertThat(ownerItems.get(0).getId(), equalTo(bookings.get(0).getId()));
    }
}