package ru.practicum.shareit.item.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Deprecated
@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    @Getter
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        log.debug("Created new item: {}", item);
        return item;
    }

    @Override
    public Item update(Long id, Item item) {
        if (items.containsKey(id)) {
            Item itemToUpdate = items.get(id);
            if (item.getName() != null) {
                if (!itemToUpdate.getName().isEmpty()) {
                    itemToUpdate.setName(item.getName());
                } else {
                    throw new BadRequestException("Name cannot be empty");
                }
            }
            if (item.getDescription() != null) {
                if (!item.getDescription().isEmpty()) {
                    itemToUpdate.setDescription(item.getDescription());
                } else {
                    throw new BadRequestException("Description cannot be empty");
                }
            }
            if (item.getAvailable() != null) {
                itemToUpdate.setAvailable(item.getAvailable());
            }
            items.put(id, itemToUpdate);
            log.debug("Item updated: {}", itemToUpdate);
            return itemToUpdate;
        } else {
            throw new NotFoundException("Item with id:" + item.getId() + " not found");
        }
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(Long id) {
        return Optional.ofNullable(items.get(id)).orElseThrow(() -> new NotFoundException("Item with id:" + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(items.remove(id)).orElseThrow(() -> new NotFoundException("Item with id:" + id + " not found"));
    }

    @Override
    public List<Item> findAll(Long userId) {
        return items.values().stream().filter(entry -> userId.equals(entry.getOwner().getId())).toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream().filter(entry -> (entry.getName().toLowerCase().contains(text.toLowerCase()) ||
                entry.getDescription().toLowerCase().contains(text.toLowerCase())) && entry.getAvailable().equals(true)).toList();
    }
}
