package ru.practicum.shareit.item;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.CommentMapper.mapToListCommentsDto;
import static ru.practicum.shareit.item.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDtoForCreate;

@Service
public class ItemService {
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemService(ItemRepository itemRepo,
                       UserRepository userRepo,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDtoForCreate addNewItem(long userId, ItemDtoForCreate itemDtoForCreate) {
        if (userRepo.existsById(userId)) {
            Item item = ItemMapper.mapToItem(itemDtoForCreate);
            item.setOwnerId(userRepo.findById(userId).orElseThrow().getId());
            itemRepo.save(item);
            return mapToItemDtoForCreate(item);
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    @Transactional(readOnly = true)
    public ItemDto getById(long itemId, long ownerId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        List<Comment> comments = new ArrayList<>();
        try {
            comments = commentRepository.findCommentByItem_Id(itemId);
        } catch (InvalidDataAccessResourceUsageException e) {
            new ArrayList<>();
        }
        List<CommentDto> commentsDto = mapToListCommentsDto(comments);
        if (ownerId == item.getOwnerId()) {
            List<Booking> bookings = bookingRepository.findBookingByItem_Id(itemId);
            return mapToItemDto(item, createLastBooker(bookings, itemId), createNextBooker(bookings, itemId), commentsDto);
        } else {
            return mapToItemDto(item, null, null, commentsDto);
        }
    }

    @Transactional
    public ItemDtoForCreate update(long itemId, long userId, ItemDtoForCreate itemDtoForCreate) {
        Item itemExisted = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        Item item = mapToItem(itemDtoForCreate);
        if (itemExisted.getOwnerId() == userId) {
            if (item.getId() == null) {
                item.setId(itemExisted.getId());
            }
            if (item.getName() == null) {
                item.setName(itemExisted.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(itemExisted.getDescription());
            }
            if (item.getOwnerId() == null) {
                item.setOwnerId(itemExisted.getOwnerId());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(itemExisted.getAvailable());
            }
            return mapToItemDtoForCreate(itemRepo.save(item));
        } else {
            throw new NotFoundException("User don't have this item");
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsOfOneUser(long userId) {
        List<ItemDto> result = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findBookingByItem_OwnerId(userId);
        if (userRepo.existsById(userId)) {
            List<Item> allItemsOfOneUser = itemRepo.findItemsByOwnerIdOrderById(userId);
            for (Item item : allItemsOfOneUser) {
                List<Comment> comments = new ArrayList<>();
                try {
                    comments = commentRepository.findCommentByItem_Id(item.getId());
                } catch (InvalidDataAccessResourceUsageException ex) {
                    new ArrayList<>();
                }
                List<CommentDto> commentsDto = mapToListCommentsDto(comments);
                result.add(mapToItemDto(item, createLastBooker(bookings, item.getId()),
                        createNextBooker(bookings, item.getId()), commentsDto));
            }
            return result;
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDtoForCreate> searchItem(String text) {
        List<ItemDtoForCreate> result = new ArrayList<>();
        if (!text.isBlank()) {
            List<Item> itemsByNameOrDescriptionLikeIgnoreCase = itemRepo.search(text);
            for (Item item : itemsByNameOrDescriptionLikeIgnoreCase) {
                result.add(mapToItemDtoForCreate(item));
            }
        } else {
            result = new ArrayList<>();
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
    public CommentDto createComment(long itemId, long userId, Comment comment) {
        List<Booking> bookings = bookingRepository.findBookingByBooker_IdAndItem_Id(userId, itemId);
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                Item item = itemRepo.findById(itemId).orElseThrow();
                User author = userRepo.findById(userId).orElseThrow();
                comment.setItem(item);
                comment.setAuthor(author);
                break;
            } else {
                throw new ValidationException("Booking not end!");
            }
        }
        return mapToCommentDto(commentRepository.save(comment));
    }
}
