package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemRequestOutDto add(long userId, ItemRequestInDto requestInDto) {
        User requester = checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestInDto, requester);
        return ItemRequestMapper.toItemRequestOutDto(itemRequestStorage.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestOutDto> getByOwner(long userId) {
        checkUserExist(userId);
        List<ItemRequest> requests = itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(userId);
        return toListRequestOutDto(requests);
    }

    @Override
    public List<ItemRequestOutDto> getAll(long userId, Pageable pageRequest) {
        checkUserExist(userId);
        List<ItemRequest> requests = itemRequestStorage.findAllByOtherUsers(userId, pageRequest);
        return toListRequestOutDto(requests);
    }

    @Override
    public ItemRequestOutDto getById(long userId, long requestId) {
        checkUserExist(userId);
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запрос с id = %s не найден!", requestId)));
        return ItemRequestMapper.toItemRequestOutDto(itemRequest,
                itemStorage.findAllByRequestId(itemRequest.getId()));
    }

    private List<ItemRequestOutDto> toListRequestOutDto(List<ItemRequest> requests) {
        List<ItemRequestOutDto> requestsOut;
        requestsOut = requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestOutDto(request,
                        itemStorage.findAllByRequestId(request.getId())))
                .collect(Collectors.toList());
        return requestsOut;
    }

    public User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId)));
    }

    public void checkUserExist(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId));
        }
    }

}