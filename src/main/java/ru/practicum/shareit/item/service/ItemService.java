package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto) throws IllegalAccessException;

    List<ItemDto> getAllItemsOfOneUser(long userId);

    List<ItemDto> searchItem(String text);

    ItemDto getById(long itemId);
}
