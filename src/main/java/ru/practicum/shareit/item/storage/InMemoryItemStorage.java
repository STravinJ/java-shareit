package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idItem = 0L;

    @Override
    public Item save(Item item) {

        item.setId(++idItem);
        items.put(item.getId(), item);

        log.info("Добавление вещи: {}", item);

        return item;

    }

    @Override
    public void deleteById(Long itemId) {

        items.remove(itemId);

    }

    @Override
    public Collection<Item> findAll() {

        log.info("Получение всех вещей.");
        return items.values();

    }

    @Override
    public void update(Item item) {

        items.remove(item.getId());
        items.put(item.getId(), item);

        log.info("Обновленние вещи: {}", item);

    }

    @Override
    public Optional<Item> findById(Long itemId) {

        log.info("Получение вещи по id.");

        return Optional.ofNullable(items.get(itemId));

    }

    @Override
    public Collection<Item> searchItemsByText(String text) {

        Collection<Item> findedItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                findedItems.add(item);
            };
        }

        return findedItems;

    }

    @Override
    public List<Item> findItemsByOwnerIdOrderById(Long userId) {

        List<Item> findedItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                findedItems.add(item);
            };
        }

        return findedItems;

    }

    @Override
    public List<Item> findAllByRequestId(Long requestId) {

        List<Item> findedItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getRequest().getId().equals(requestId)) {
                findedItems.add(item);
            };
        }

        return findedItems;

    }

}
