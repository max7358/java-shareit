package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.common.storage.CommonStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends CommonStorage<Item> {
    List<Item> findAll(Long userId);

    List<Item> searchItems(String text);
}
