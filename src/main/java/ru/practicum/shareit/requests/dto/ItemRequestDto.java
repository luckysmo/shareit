package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private UserDto requester;
    private LocalDateTime created;
}
