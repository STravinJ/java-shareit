package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        checkItemDto(itemDto);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestStorage.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Запрос с id = %d не найден!", itemDto.getRequestId())));
        }
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, user, request)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        checkUser(userId);
        Item oldItem = checkItem(itemDto.getId());
        if (oldItem.getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Пользователь с id %s не является владельцем " +
                    "вещи id %s!", userId, itemDto.getId()));
        }
        if (itemDto.getName() != null && !(itemDto.getName().trim().isEmpty())) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !(itemDto.getDescription().trim().isEmpty())) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        itemStorage.update(oldItem);
        return ItemMapper.toItemDto(oldItem);
    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long userId) {
        checkUser(userId);
        List<Item> itemList = itemStorage.findItemsByOwnerIdOrderById(userId);
        List<ItemDto> itemDtoResponseList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDto itemResponseDto = ItemMapper.toItemDto(item);
            itemDtoResponseList.add(itemResponseDto);
        }
        return itemDtoResponseList;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemStorage.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
    }

    private Item checkItem(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    private void checkItemDto(ItemDto itemDto) {
        String error = null;
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            error = "У вещи должно быть название!";
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            error = "У вещи должно быть описание!";
        }
        if (itemDto.getAvailable() == null) {
            error = "Отсутствует статус доступности вещи для аренды!";
        }

        if (error != null) {
            throw new ValidationException(error);
        }
    }

}
