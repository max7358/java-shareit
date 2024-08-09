package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("You do not own this item");
        }
        item = itemStorage.update(itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(itemStorage.findById(id));
    }

    public List<ItemDto> getItems(Long userId) {
        userStorage.findById(userId);
        return itemStorage.findAll(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        } else {
            List<Item> items = itemStorage.searchItems(text);
            return items.stream().map(ItemMapper::toItemDto).toList();
        }
    }
}
