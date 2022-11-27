package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    CommentRepository commentRepository;

    User user;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, itemRequestRepository,
                commentRepository);
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.of(2022, 10, 14, 13, 17, 29));
        item = new Item(1L, "Item", "item description", true, user, null);

    }

    @Test
    void add() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto1 = itemService.add(1L, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(item.getId(), itemDto1.getId());
        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getDescription(), itemDto1.getDescription());
        assertEquals(item.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void update() {
        Item updatedItem = new Item(item.getId(), "updateItemName", "updateItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        ItemDto itemDto = ItemMapper.toItemDto(updatedItem);
        ItemDto itemDto1 = itemService.update(1, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(updatedItem.getId(), itemDto1.getId());
        assertEquals(updatedItem.getName(), itemDto1.getName());
        assertEquals(updatedItem.getDescription(), itemDto1.getDescription());
        assertEquals(updatedItem.getAvailable(), itemDto1.getAvailable());

    }

    @Test
    void findById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    }

    @Test
    void getAllItemsByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
    }

    @Test
    void addItemWithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.add(10, itemDto));

        assertEquals("Пользователь с id = 10 не найден!", exception.getMessage());
    }

    @Test
    void updateItemWithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("Пользователь с id = 10 не найден!", exception.getMessage());
    }

    @Test
    void updateItemWithWrongItemId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("Вещь с id = 1 не найдена!", exception.getMessage());
    }

    @Test
    void findItemWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.findById(10, user.getId()));

        assertEquals("Вещь с id = 10 не найдена!", exception.getMessage());
    }

    @Test
    void addItemWithWrongItemName() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setName("");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("У вещи должно быть название!", exception.getMessage());
    }

    @Test
    void addItemWithWrongItemDescription() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setDescription("");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("У вещи должно быть описание!", exception.getMessage());
    }

    @Test
    void addItemWithNullAvailable() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setAvailable(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("Отсутствует статус доступности вещи для аренды!", exception.getMessage());
    }

}