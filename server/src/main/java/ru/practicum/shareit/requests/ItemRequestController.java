package ru.practicum.shareit.requests;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithItems;

import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getRequestsOfCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsOfCurrentUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(@RequestParam(required = false, defaultValue = "0")
                                                        Integer from,
                                                        @RequestParam(required = false, defaultValue = "20")
                                                        Integer size,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequest(@PathVariable long requestId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
