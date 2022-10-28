package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.CommentMapper.mapToListCommentsDto;
import static ru.practicum.shareit.item.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDtoForCreate;
import static ru.practicum.shareit.item.ItemMapper.mapToListItemDtoForCreate;

@Transactional(readOnly = true)
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository,
                       ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = itemRequestRepository;
    }

    @Transactional
    public ItemDtoForCreate addNewItem(long userId, ItemDtoForCreate itemDtoForCreate) {
        if (userRepository.existsById(userId)) {
            Item item = ItemMapper.mapToItem(itemDtoForCreate);
            if (itemDtoForCreate.getRequestId() != null) {
                ItemRequest request = requestRepository.findById(itemDtoForCreate.getRequestId())
                        .orElseThrow(() -> new NotFoundException("Request not found!!!"));
                item.setRequest(request);
            }
            item.setOwner(userRepository.findById(userId).orElseThrow());
            itemRepository.save(item);
            return mapToItemDtoForCreate(item);
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    public ItemDto getById(long itemId, long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        List<Comment> comments = commentRepository.findCommentByItem_Id(itemId);
        List<CommentDto> commentsDto = mapToListCommentsDto(comments);
        if (ownerId == item.getOwner().getId()) {
            List<Booking> bookings = bookingRepository.findBookingByItem_Id(itemId);
            return mapToItemDto(item, createLastBooker(bookings, itemId), createNextBooker(bookings, itemId), commentsDto);
        } else {
            return mapToItemDto(item, null, null, commentsDto);
        }
    }

    @Transactional
    public ItemDtoForCreate update(long itemId, long userId, ItemDtoForCreate itemDtoForCreate) {
        Item itemExisted = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        Item item = mapToItem(itemDtoForCreate);
        if (itemExisted.getOwner().getId() == userId) {
            if (item.getId() == null) {
                item.setId(itemExisted.getId());
            }
            if (item.getName() != null) {
                itemExisted.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemExisted.setDescription(item.getDescription());
            }
            if (item.getOwner() != null) {
                itemExisted.setOwner(item.getOwner());
            }
            if (item.getAvailable() != null) {
                itemExisted.setAvailable(item.getAvailable());
            }
            if (item.getRequest() != null) {
                itemExisted.setRequest(item.getRequest());
            }
            return mapToItemDtoForCreate(itemExisted);
        } else {
            throw new NotFoundException("User don't have this item");
        }
    }

    public List<ItemDtoForCreate> searchItem(String text, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDtoForCreate> result;
        if (!text.isBlank()) {
            List<Item> itemsByNameOrDescriptionLikeIgnoreCase = itemRepository.search(text, pageable);
            result = mapToListItemDtoForCreate(itemsByNameOrDescriptionLikeIgnoreCase);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    private BookingDtoForOwner createLastBooker(List<Booking> bookings, long itemId) {
        BookingDtoForOwner last = new BookingDtoForOwner();
        for (Booking booking : bookings) {
            if (booking.getItem().getId().equals(itemId)) {
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    last.setId(booking.getId());
                    last.setBookerId(booking.getBooker().getId());
                    last.setStartTime(booking.getStart());
                    last.setEndTime(booking.getEnd());
                    break;
                }
            } else {
                return null;
            }
        }
        return last;
    }

    private BookingDtoForOwner createNextBooker(List<Booking> bookings, long itemId) {
        BookingDtoForOwner next = new BookingDtoForOwner();
        for (Booking booking : bookings) {
            if (booking.getItem().getId().equals(itemId)) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    next.setId(booking.getId());
                    next.setBookerId(booking.getBooker().getId());
                    next.setStartTime(booking.getStart());
                    next.setEndTime(booking.getEnd());
                    break;
                }
            } else {
                return null;
            }
        }
        return next;
    }

    @Transactional
    public CommentDto createComment(long itemId, long userId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findBookingByBooker_IdAndItem_Id(userId, itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException("Item not found!!!"));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!!!"));
        Comment comment = mapToComment(commentDto, author, item);
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                item = itemRepository.findById(itemId).orElseThrow();
                author = userRepository.findById(userId).orElseThrow();
                comment.setItem(item);
                comment.setAuthor(author);
                break;
            } else {
                throw new ValidationException("Booking not end!");
            }
        }
        return mapToCommentDto(commentRepository.save(comment));
    }

    public List<ItemDto> getAllItemsOfOneUser(long userId, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDto> result = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findBookingByItem_OwnerId(userId);

        if (userRepository.existsById(userId)) {
            List<Item> allItemsOfOneUser = itemRepository.findItemsByOwnerIdOrderById(userId, pageable);
            for (Item item : allItemsOfOneUser) {
                List<Comment> comments = commentRepository.findCommentByItem_Id(item.getId());
                List<CommentDto> commentsDto = mapToListCommentsDto(comments);
                result.add(mapToItemDto(item, createLastBooker(bookings, item.getId()),
                        createNextBooker(bookings, item.getId()), commentsDto));
            }
            return result;
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }
}
