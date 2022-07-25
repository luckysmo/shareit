package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repo.ItemRepoImpl;
import ru.practicum.shareit.user.repo.UserRepoImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Validated
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepoImpl itemRepo;
    @Autowired
    private UserRepoImpl userRepo;
    @Autowired
    private ItemMapper mapper;

    public ItemDto addNewItem(long userId, @Valid Item item) {
        if (userRepo.isExist(userId)) {
            itemRepo.add(item);
            item.setOwner(userRepo.getById(userId).get());
            return mapper.mapToItemDto(item);
        } else {
            throw new NotFoundException("User with id " + userId + " not found!");
        }
    }

    public ItemDto getById(long itemId) {
        return mapper.mapToItemDto(itemRepo.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found!!!")));
    }

    public ItemDto update(long itemId, long userId, Item item) {
        Item itemExisted = itemRepo.getById(itemId)
                .get();
        if (itemRepo.getById(itemId).get().getOwner().getId() == userId) {
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
            return mapper.mapToItemDto(itemRepo.update(item));
        } else {
            throw new NotFoundException("User don't have this item");
        }
    }

    public List<ItemDto> getAllItemsOfOneUser(long userId) {
        List<ItemDto> result = new ArrayList<>();
        if (userRepo.isExist(userId)) {
            List<Item> allItemsOfOneUser = itemRepo.getAllItemsOfOneUser(userId);
            for (Item item : allItemsOfOneUser) {
                result.add(mapper.mapToItemDto(item));
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
                    if (item.getAvailable() == true) {
                        result.add(mapper.mapToItemDto(item));
                    }
                }
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
