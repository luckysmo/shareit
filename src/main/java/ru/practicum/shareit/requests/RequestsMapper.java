package ru.practicum.shareit.requests;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

@Service
public class RequestsMapper {

    public ItemRequest mapToUser(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getCreated()
        );
    }

    public ItemRequestDto mapToItemDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }
}
