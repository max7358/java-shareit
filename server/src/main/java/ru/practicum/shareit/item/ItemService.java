package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentSaveDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService,
                       @Lazy BookingService bookingService, CommentRepository commentRepository, @Lazy ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.itemRequestService = itemRequestService;
    }

    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.getUserById(userId);
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, userDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestService.getRequest(itemDto.getRequestId()), userDto);
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with id:" + itemId + " not found"));
        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NotFoundException("You do not own this item");
        }
        if (itemDto.getName() != null) {
            if (!itemToUpdate.getName().isEmpty()) {
                itemToUpdate.setName(itemDto.getName());
            } else {
                throw new BadRequestException("Name cannot be empty");
            }
        }
        if (itemDto.getDescription() != null) {
            if (!itemDto.getDescription().isEmpty()) {
                itemToUpdate.setDescription(itemDto.getDescription());
            } else {
                throw new BadRequestException("Description cannot be empty");
            }
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        Item item = itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(item);
    }

    public ItemAllDto getItem(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with id:" + id + " not found"));
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            List<BookingDto> bookings = bookingService.getBookingsByItem(item.getId());
            lastBooking = findLast(bookings);
            nextBooking = findNext(bookings);
        }
        return ItemMapper.toItemAllDto(itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with id:" + id + " not found")),
                lastBooking, nextBooking, findComments(id));
    }

    public List<ItemAllDto> getItems(Long userId) {
        userService.getUserById(userId);
        Map<Long, List<BookingDto>> bookingsMap = bookingService.getBookingsByOwner(userId, Optional.empty())
                .stream()
                .collect(Collectors.groupingBy((BookingDto booking) -> booking.getItem().getId()));
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> ItemMapper.toItemAllDto(item, findLast(bookingsMap.get(item.getId())), findNext(bookingsMap.get(item.getId())), findComments(item.getId()))).toList();
    }

    private List<CommentDto> findComments(Long itemId) {
        return commentRepository.findAllByItem_IdOrderByCreated(itemId).stream()
                .map(CommentMapper::toCommentDto).toList();
    }

    private BookingDto findLast(List<BookingDto> booking) {
        if (booking == null) {
            return null;
        } else {
            return booking.stream()
                    .filter(bk -> bk.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(BookingDto::getEnd))
                    .orElse(null);
        }
    }

    private BookingDto findNext(List<BookingDto> booking) {
        if (booking == null) {
            return null;
        } else {
            return booking.stream()
                    .filter(bk -> bk.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(BookingDto::getEnd))
                    .orElse(null);
        }
    }

    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        } else {
            List<Item> items = itemRepository.search(text);
            return items.stream().map(ItemMapper::toItemDto).toList();
        }
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentSaveDto commentDto) {
        ItemDto item = getItem(itemId, userId);
        UserDto user = userService.getUserById(userId);
        List<BookingDto> bookings = bookingService.getBookingsByUserAndItem(userId, itemId);
        if (!bookings.isEmpty()) {
            Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new BadRequestException("User can't make comments");
        }
    }

    public List<ItemDto> getItemsByRequestId(Long id) {
        return itemRepository.findByRequestId(id).stream().map(ItemMapper::toItemDto).toList();
    }

    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIn(requests).stream().map(ItemMapper::toItemDto).toList();
    }
}
