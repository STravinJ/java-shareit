package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperTest {

    User user;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.of(2022, 10, 14, 13, 44, 22));
        item = new Item(1L, "Item", "item description", true, user, itemRequest);
    }

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItem() {
        Item item1 = ItemMapper.toItem(ItemMapper.toItemDto(item), user, itemRequest);
        assertNotNull(item1);
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getDescription(), item1.getDescription());
        assertEquals(item.getAvailable(), item1.getAvailable());
        assertEquals(itemRequest.getId(), item1.getRequest().getId());
        assertEquals(user.getId(), item1.getOwner().getId());
    }
}