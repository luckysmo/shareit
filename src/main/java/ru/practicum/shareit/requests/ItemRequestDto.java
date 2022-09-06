package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}