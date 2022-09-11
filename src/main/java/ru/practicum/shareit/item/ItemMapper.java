package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItem(ItemDtoForCreate itemDtoForCreate) {
        return new Item(
                itemDtoForCreate.getId(),
                itemDtoForCreate.getName(),
                itemDtoForCreate.getDescription(),
                itemDtoForCreate.getAvailable(),
                null,
                null
        );
    }


    public static ItemDtoForCreate mapToItemDtoForCreate(Item item) {
        return new ItemDtoForCreate(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
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

    public static List<ItemDtoForCreate> mapToListItemDtoForCreate(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDtoForCreate)
                .collect(Collectors.toList());
    }
}
