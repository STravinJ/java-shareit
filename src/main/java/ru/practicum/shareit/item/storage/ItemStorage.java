package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item save(Item item);

    void deleteById(Long itemId);

    Collection<Item> findAll();

    void update(Item item);

    Optional<Item> findById(Long itemId);

    Collection<Item> searchItemsByText(String text);

    List<Item> findItemsByOwnerIdOrderById(Long userId);

    List<Item> findAllByRequestId(Long requestId);

}
