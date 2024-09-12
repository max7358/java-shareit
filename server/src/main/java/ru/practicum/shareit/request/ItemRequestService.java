package ru.practicum.shareit.request;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestService(ItemRequestRepository repository, UserService userService, @Lazy ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        UserDto userDto = userService.getUserById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest request = repository.save(ItemRequestMapper.toItemRequest(itemRequestDto, userDto));
        return ItemRequestMapper.toItemRequestDto(request);
    }

    public List<ItemRequestDto> getRequests(Long userId) {
        List<ItemRequest> requests = repository.findAllByRequestorIdOrderByCreatedDesc(userId);
        Map<Long, List<ItemDto>> items = itemService.getItemsByRequests(requests).stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream().map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, items.get(itemRequest.getId()))).toList();
    }

    public List<ItemRequestDto> getAllRequests(Long userId) {
        return repository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId).stream().map(ItemRequestMapper::toItemRequestDto).toList();
    }

    public ItemRequestDto getRequest(Long id) {
        ItemRequest request = repository.findById(id).orElseThrow(() -> new NotFoundException("Request with id:" + id + " not found"));
        List<ItemDto> items = itemService.getItemsByRequestId(id);
        return ItemRequestMapper.toItemRequestDto(request, items);
    }
}