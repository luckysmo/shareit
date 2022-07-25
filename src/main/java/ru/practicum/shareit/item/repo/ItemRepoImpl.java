package ru.practicum.shareit.item.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ItemRepoImpl {

    private static long idCounter = 0;
    private final HashMap<Long, Item> items = new HashMap<>();

    private void setIdCounter(Item item) {
        ++idCounter;
        item.setId(idCounter);
    }

    public void add(Item item) {
        setIdCounter(item);
        items.put(item.getId(), item);
        log.debug("Item {} add", item);
    }

    public Optional<Item> getById(long itemId) {
        log.debug("returned item with id {}", itemId);
        return Optional.ofNullable(Optional.of(items.get(itemId)).orElseThrow(() -> new NotFoundException("Item with id" + itemId + " not found!")));
    }

    public Item update(Item item) {
        items.replace(item.getId(), item);
        log.debug("updated item {}", item);
        return getById(item.getId()).orElseThrow(() -> new NotFoundException("Item with id" + item.getId() + " not found!"));
    }

    public List<Item> getAllItemsOfOneUser(long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                result.add(item);
            }
        }
        log.debug("returned all items of user with ID {}", userId);
        return result;
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
