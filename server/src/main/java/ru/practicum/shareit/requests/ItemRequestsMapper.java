package ru.practicum.shareit.requests;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithItems;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.mapToUser;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestsMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                mapToUser(itemRequestDto.getRequester()),
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                mapToUserDto(itemRequest.getRequester()),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestDtoWithItems mapToItemRequestDtoWithItems(ItemRequest itemRequest,
                                                                       List<ItemDtoForCreate> items) {
        return new ItemRequestDtoWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                mapToUserDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                items
        );
    }
}