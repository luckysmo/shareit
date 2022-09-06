package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItem(ItemDtoForCreate itemDtoForCreate) {
        return new Item(
                itemDtoForCreate.getId(),
                itemDtoForCreate.getName(),
                itemDtoForCreate.getDescription(),
                itemDtoForCreate.getAvailable(),
                itemDtoForCreate.getOwnerId(),
                itemDtoForCreate.getRequestId()
        );
    }


    public static ItemDtoForCreate mapToItemDtoForCreate(Item item) {
        return new ItemDtoForCreate(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static ItemDto mapToItemDto(Item item,
                                       BookingDtoForOwner last,
                                       BookingDtoForOwner next,
                                       List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                last,
                next,
                comments
        );
    }
}
