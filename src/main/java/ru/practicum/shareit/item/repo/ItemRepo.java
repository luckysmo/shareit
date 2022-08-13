package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepo {
    void add(Item item);

    Optional<Item> getById(long itemId);

    Item update(Item item);

    List<Item> getAllItemsOfOneUser(long userId);

    List<Item> getAll();
}
