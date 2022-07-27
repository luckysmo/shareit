package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repo.ItemRepoImpl;
import ru.practicum.shareit.user.repo.UserRepoImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;

@Validated
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepoImpl itemRepo;
    private final UserRepoImpl userRepo;

    public ItemServiceImpl(ItemRepoImpl itemRepo, UserRepoImpl userRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    public ItemDto addNewItem(long userId, @Valid Item item) {
        if (userRepo.isExist(userId)) {
            itemRepo.add(item);
            item.setOwner(userRepo.getById(userId).orElseThrow());
            return mapToItemDto(item);
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    public ItemDto getById(long itemId) {
        return mapToItemDto(itemRepo.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!")));
    }

    public ItemDto update(long itemId, long userId, Item item) {
        Item itemExisted = itemRepo.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!"));
        if (itemExisted.getOwner().getId() == userId) {
            if (item.getId() == null) {
                item.setId(itemExisted.getId());
            }
            if (item.getName() == null) {
                item.setName(itemExisted.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(itemExisted.getDescription());
            }
            if (item.getOwner() == null) {
                item.setOwner(itemExisted.getOwner());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(itemExisted.getAvailable());
            }
            return mapToItemDto(itemRepo.update(item));
        } else {
            throw new NotFoundException("User don't have this item");
        }
    }

    public List<ItemDto> getAllItemsOfOneUser(long userId) {
        List<ItemDto> result = new ArrayList<>();
        if (userRepo.isExist(userId)) {
            List<Item> allItemsOfOneUser = itemRepo.getAllItemsOfOneUser(userId);
            for (Item item : allItemsOfOneUser) {
                result.add(mapToItemDto(item));
            }
            return result;
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> result = new ArrayList<>();
        if (!text.isEmpty()) {
            for (Item item : itemRepo.getAll()) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                    if (item.getAvailable()) {
                        result.add(mapToItemDto(item));
                    }
                }
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
