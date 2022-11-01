package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestDto requestDto) {
        return client.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAllForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @PathVariable Long requestId) {
        return client.getRequest(userId, requestId);
    }
}
