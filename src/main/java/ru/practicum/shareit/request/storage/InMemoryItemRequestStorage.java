package ru.practicum.shareit.request.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Component("InMemoryItemRequestStorage")
@Slf4j
public class InMemoryItemRequestStorage implements ItemRequestStorage {
    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public List<ItemRequest> findAllByOtherUsers(Long userId, Pageable pageRequest) {
        return null;
    }

    @Override
    public List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId) {
        return null;
    }

    @Override
    public Optional<ItemRequest> findById(Long itemRequestId) {
        return Optional.empty();
    }
}
