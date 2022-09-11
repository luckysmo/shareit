package ru.practicum.shareit.requests;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestsMapper {

    public static ItemRequest mapToUser(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequesterId(),
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto mapToItemDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequesterId(),
                itemRequest.getCreated()
        );
    }
}
