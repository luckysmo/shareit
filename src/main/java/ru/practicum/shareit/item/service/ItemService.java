package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface ItemService {

    ItemDto addNewItem(long userId, @Valid Item item);

    ItemDto update(long itemId, long userId, Item item) throws IllegalAccessException;

    List<ItemDto> getAllItemsOfOneUser(long userId);

    List<ItemDto> searchItem(String text);

    ItemDto getById(long itemId);
}
