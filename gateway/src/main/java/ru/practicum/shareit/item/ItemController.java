package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentSaveDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating new item: {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId, @Valid @RequestBody CommentSaveDto commentDto) {
        log.info("Adding comment: {}, userId={}", commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item: {}", itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @PathVariable Long id) {
        log.info("Retrieving item: {}", id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Retrieving items");
        return itemClient.getItems(userId);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        log.info("Retrieving items with text {}", text);
        return itemClient.findItems(text);
    }
}
