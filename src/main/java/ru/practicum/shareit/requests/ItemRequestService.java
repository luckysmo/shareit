package ru.practicum.shareit.requests;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.requests.RequestsMapper.mapToItemRequest;
import static ru.practicum.shareit.requests.RequestsMapper.mapToItemRequestDto;
import static ru.practicum.shareit.requests.RequestsMapper.mapToItemRequestDtoWithItems;

@Service
public class ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestService(UserRepository userRepository,
                              ItemRequestRepository itemRequestRepository,
                              ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow();

        itemRequestDto.setRequester(user);
        itemRequestDto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = mapToItemRequest(itemRequestDto);
        return mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDtoWithItems> getRequestsOfCurrentUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found!!!", userId));
        }

        return itemRequestRepository.getItemRequestByRequester_Id(userId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemRequest -> mapToItemRequestDtoWithItems(itemRequest,
                        getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());


    }

    private List<ItemDtoForCreate> getItemsByRequestId(long requestId) {
        List<Item> items = itemRepository.getItemByRequestId(requestId);

        return items.stream()
                .map(ItemMapper::mapToItemDtoForCreate)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDtoWithItems> getAllRequests(Long from, Long size) {
        return itemRequestRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(itemRequest -> mapToItemRequestDtoWithItems(itemRequest,
                        getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found!!!", userId));
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id %d not found!!!", requestId)));
        return mapToItemRequestDto(itemRequest);
    }
}
