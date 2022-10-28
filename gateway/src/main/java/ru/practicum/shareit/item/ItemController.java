package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto itemDto,
                                          @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Adding new Item. UserId = {}", userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.info("Editing item with id = {}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @PathVariable Long itemId,
                                          @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by id = {} and userId = {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByOwnerId(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting items by ownerId = {}", userId);
        return itemClient.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItemsByText(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Searching items by text: {}", text);
        return itemClient.getAvailableByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @PathVariable Long itemId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Adding comment to item with id = {} and comment text: {}", itemId, commentDto.getText());
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
