package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemUnitTest {
    private final User booker = new User(2L, "user", "user@mail.com");
    private final User owner = new User(1L, "user2", "user2@mail.com");
    private final ItemRequest itemRequest = new ItemRequest(1L, "dex", booker, LocalDateTime.now().plusHours(1));
    private final Item item = new Item(1L, "item", "des", true, owner, itemRequest);
    private final Booking booking1 = new Booking(
            null,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            item,
            booker,
            null);
    @Mock
    BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    void whenGetItemById_thenReturnItemDtoWithId1() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(createItem(1L)));

        ItemDto byId = itemService.getById(1L, anyLong());

        assertEquals(1L, byId.getId());
    }

    @Test
    void whenGetItemById_thenCallItemRepositoryFindById() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(createItem(1L)));

        itemService.getById(1L, anyLong());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetItemById_thenThrowNotFoundException() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(1L, anyLong()));

        assertEquals("Item not found!!!", exception.getMessage());
    }

    @Test
    void whenCreateNewItem_thenThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(1L, createItemDtoForCreate(1L)));

        assertEquals("User with id " + 1 + " not found!", exception.getMessage());
    }

    @Test
    void whenGetOwnItems_thenReturnListItemDto() {
        when(bookingRepository.findBookingByItem_OwnerId(1L)).thenReturn(List.of(booking1));
        when(itemRepository.findItemsByOwnerIdOrderById(1L, PageRequest.of(0, 20)))
                .thenReturn(List.of(createItem(1L), createItem(2L)));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        List<ItemDto> items = itemService.getAllItemsOfOneUser(1L, 0, 20);

        assertEquals(2, items.size());
    }

    @Test
    void whenCreate_thenCallItemRepositorySave() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(
                Optional.of(new User(1L, "name", "email@mail.ru"))
        );
        when(itemRepository.save(any())).thenReturn(createItem(1L));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        itemService.addNewItem(anyLong(), createItemDtoForCreate(1L));

        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void whenCreateItemId1_thenReturnItemDtoId1() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(
                Optional.of(new User(1L, "name", "email@mail.ru"))
        );
        when(itemRepository.save(any())).thenReturn(createItem(1L));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemDtoForCreate itemDto = itemService.addNewItem(anyLong(), createItemDtoForCreate(1L));

        assertEquals(1L, itemDto.getId());
    }

    @Test
    void whenUpdateItemName_thenReturnUpdatedItemName() {
        Item item = createItem(1L);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        ItemDtoForCreate itemDto = itemService.update(
                1L,
                1L,
                new ItemDtoForCreate(
                        null,
                        "UpdatedItem",
                        null,
                        null,
                        null
                )
        );

        assertEquals("UpdatedItem", itemDto.getName());
        assertEquals(1L, itemDto.getId());
        assertEquals("Description1", itemDto.getDescription());
    }

    @Test
    void whenUpdateItemDescription_thenReturnUpdatedItemDescription() {
        Item item = createItem(1L);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        ItemDtoForCreate itemInputDto =
                itemService.update(
                        1L,
                        1L,
                        new ItemDtoForCreate(null,
                                null,
                                "UpdatedDescription",
                                null,
                                null
                        )
                );

        assertEquals("UpdatedDescription", itemInputDto.getDescription());
        assertEquals("Item1", itemInputDto.getName());
        assertEquals(1L, itemInputDto.getId());
    }

    @Test
    void whenUpdateItemAvailable_thenReturnUpdatedItemAvailable() {
        Item item = createItem(1L);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        ItemDtoForCreate itemDto = itemService.update(
                1L,
                1L,
                new ItemDtoForCreate(
                        null,
                        null,
                        null,
                        false,
                        null
                )
        );

        assertFalse(itemDto.getAvailable());
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId());
        assertEquals("Description1", itemDto.getDescription());
    }

    @Test
    void whenUpdateItemWithWrongUser_thenThrowNotFoundException() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 2L, createItemDtoForCreate(1L)));

        assertEquals("User don't have this item", exception.getMessage());
    }

    @Test
    void whenSearchItem_thenCallItemRepositorySearchItem() {
        List<Item> items = List.of(createItem(1L), createItem(2L), createItem(3L));

        when(itemRepository.search("Item", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(items).toList());

        itemService.searchItem("Item", 0, 20);

        verify(itemRepository, times(1))
                .search("Item", PageRequest.of(0, 20));
    }

    @Test
    void whenSearchItemExist_thenReturnListOfItems() {
        List<Item> items = List.of(createItem(1L), createItem(2L), createItem(3L));

        when(itemRepository.search("Item", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(items).toList());

        List<ItemDtoForCreate> itemsInputDto = itemService.searchItem("Item", 0, 20);

        assertEquals(3, itemsInputDto.size());
    }

    @Test
    void whenCreateComment_thenCallCommentRepositorySave() {
        User user1 = createUser(1L);
        Item item1 = createItem(1L);
        CommentDto commentDto = createCommentDto(1L, user1);
        Comment comment = new Comment(null, "text", item1, user1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        itemService.createComment(item1.getId(), user1.getId(), commentDto);

        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void whenCreateComment_thenReturnCommentDto() {
        User user1 = createUser(1L);
        Item item1 = createItem(1L);
        CommentDto commentDto = createCommentDto(1L, user1);
        Comment comment = new Comment(1L, "text", item1, user1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentOutputDto = itemService.createComment(item.getId(), user1.getId(), commentDto);

        assertEquals(comment.getId(), commentOutputDto.getId());
        assertEquals("text", commentOutputDto.getText());
        assertEquals("user", commentOutputDto.getAuthorName());
        assertEquals(LocalDateTime.now()
                        .truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_DATE_TIME),
                commentOutputDto.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_DATE_TIME));
    }

    private ItemDtoForCreate createItemDtoForCreate(Long id) {
        return new ItemDtoForCreate(
                id,
                "Item" + id,
                "Description" + id,
                true,
                itemRequest.getId()
        );
    }

    private CommentDto createCommentDto(Long id, User user) {
        return new CommentDto(
                id,
                "textComment",
                user.getName(),
                LocalDateTime.now().plusHours(1)
        );
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .name("user")
                .email("user" + "@email.ru")
                .build();
    }

    private Item createItem(Long id) {
        return new Item(
                id,
                "Item" + id,
                "Description" + id,
                true,
                owner,
                itemRequest
        );
    }
}