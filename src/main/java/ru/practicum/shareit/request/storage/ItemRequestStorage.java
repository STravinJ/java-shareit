package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestStorage {

    ItemRequest save(ItemRequest itemRequest);

    List<ItemRequest> findAllByOtherUsers(Long userId, Pageable pageRequest);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    Optional<ItemRequest> findById(Long itemRequestId);

}
