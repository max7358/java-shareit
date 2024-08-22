package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating new item: {}", itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Updating item: {}", itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Long id) {
        log.info("Retrieving item: {}", id);
        return itemService.getItem(id);
    }

    @GetMapping
    public List<ItemDatesDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Retrieving items");
        return itemService.getItems(userId);

    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("Retrieving items with text {}", text);
        return itemService.findItems(text);
    }
}
