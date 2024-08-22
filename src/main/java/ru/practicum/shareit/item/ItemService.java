package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
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

    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with id:" + id + " not found")));
    }

    public List<ItemDatesDto> getItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id:" + userId + " not found"));
        Map<Long, List<Booking>> bookingsMap = bookingRepository.findBookingsByItemOwnerOrderByStartDesc(user)
                .stream()
                .collect(Collectors.groupingBy((Booking booking) -> booking.getItem().getId()));
        List<ItemDatesDto> list = itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> ItemMapper.toItemDatesDto(item, findLast(bookingsMap.get(item.getId())), findNext(bookingsMap.get(item.getId())))).toList();
        return list;
    }

    private Booking findLast(List<Booking> booking) {
        if (booking == null) {
            return null;
        } else {
            return booking.stream()
                    .filter(bk -> bk.getEnd().isBefore(LocalDateTime.now()))
                    .max(comparing(Booking::getEnd))
                    .orElse(null);
        }
    }

    private Booking findNext(List<Booking> booking) {
        if (booking == null) {
            return null;
        } else {
            return booking.stream()
                    .filter(bk -> bk.getStart().isAfter(LocalDateTime.now()))
                    .min(comparing(Booking::getEnd))
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
}
