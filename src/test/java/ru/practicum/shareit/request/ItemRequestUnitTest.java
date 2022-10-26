package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestUnitTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestService itemRequestService;

    @Test
    void whenGetRequestById_thenCallItemRequestRepositoryFindById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest(1L)));

        itemRequestService.getRequestById(1L, 1L);

        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void whenGetItemRequestById_thenReturnItemRequestDtoWithTime() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest(1L)));
        when(itemRepository.getItemByRequestId(1L)).thenReturn(List.of(createItem(1L)));

        ItemRequestDtoWithItems requestById = itemRequestService.getRequestById(1L, 1L);

        assertEquals(1L, requestById.getId());
        assertEquals("description", requestById.getDescription());
    }

    @Test
    void whenGetByIdNotExistItemRequest_thenThrowNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L)
        );

        assertEquals("Request with id 1 not found!!!", exception.getMessage());
    }

    @Test
    void whenCreateItemRequest_thenCallItemRequestRepositorySave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L)));
        when(itemRequestRepository.save(any())).thenReturn(createItemRequest(1L));

        itemRequestService.addRequest(1L, createItemRequestDto(1L));

        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void whenCreateItemRequest_thenReturnItemRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L)));
        when(itemRequestRepository.save(any())).thenReturn(createItemRequest(1L));

        ItemRequestDto itemRequestDto = itemRequestService.addRequest(1L, createItemRequestDto(1L));

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    void whenGetRequestsOfCurrentUser_thenReturnListItemRequestsDto() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.getItemRequestByRequester_Id(
                1L,
                Sort.by(Sort.Direction.DESC, "created"))
        )
                .thenReturn(List.of(createItemRequest(1L),
                        createItemRequest(2L),
                        createItemRequest(3L)));

        List<ItemRequestDtoWithItems> requestsOfCurrentUser = itemRequestService.getRequestsOfCurrentUser(1L);

        assertEquals(3, requestsOfCurrentUser.size());
    }

    @Test
    void whenGetRequestsOfCurrentUser_thenCallItemRequestsRepository() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.getItemRequestByRequester_Id(
                1L,
                Sort.by(Sort.Direction.DESC, "created"))
        )
                .thenReturn(List.of(createItemRequest(1L),
                        createItemRequest(2L),
                        createItemRequest(3L)));

        itemRequestService.getRequestsOfCurrentUser(1L);

        verify(itemRequestRepository, times(1))
                .getItemRequestByRequester_Id(anyLong(), any());
    }

    @Test
    void whenGetAllRequests_thenReturnListItemRequestsDto() {
        ItemRequest itemRequest1 = createItemRequest(1L);
        ItemRequest itemRequest2 = createItemRequest(2L);
        ItemRequest itemRequest3 = createItemRequest(3L);

        itemRequest1.setRequester(createUser(1L));
        itemRequest2.setRequester(createUser(2L));
        itemRequest3.setRequester(createUser(3L));

        when(itemRequestRepository.findAllWithoutUserRequests(4L,
                PageRequest.of(0, 20, Sort.by("created").descending()))
        )
                .thenReturn(new PageImpl<>(List.of(itemRequest1, itemRequest2, itemRequest3)));

        List<ItemRequestDtoWithItems> allRequests = itemRequestService.getAllRequests(4L, 0, 20);

        verify(itemRequestRepository, times(1))
                .findAllWithoutUserRequests(
                        4L, PageRequest.of(0, 20, Sort.by("created").descending())
                );
        assertEquals(3, allRequests.size());
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .name("user" + id)
                .email("user" + id + "@email.ru")
                .build();
    }

    private ItemRequest createItemRequest(Long id) {
        return new ItemRequest(id,
                "description",
                new User(2L,"userDto2","userdto@mail.ru"),
                LocalDateTime.now());
    }

    private ItemRequestDto createItemRequestDto(Long id) {
        return new ItemRequestDto(id,
                "description",
                new UserDto(null,"user", "wop@mail.ru"),
                LocalDateTime.now());
    }

    private Item createItem(Long id) {
        return new Item(
                id,
                "Item" + id,
                "Description" + id,
                true,
                new User(),
                null
        );
    }
}