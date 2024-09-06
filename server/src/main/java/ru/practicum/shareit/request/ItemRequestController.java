package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create request: {}", itemRequestDto);
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests: {}", userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all requests");
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable Long id) {
        log.info("Get request: {}", id);
        return itemRequestService.getRequest(id);
    }
}