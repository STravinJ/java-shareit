package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto) {
        checkBookingDate(bookingRequestDto);
        User booker = checkUser(userId);
        Item item = checkItem(bookingRequestDto.getItemId());
        checkItemOwner(userId, item);
        checkItemAvailable(item);
        return BookingMapper.toBookingResponseDto(bookingStorage.save(BookingMapper.toBooking(bookingRequestDto,
                item, booker)));
    }

    @Override
    public BookingResponseDto approve(long userId, long bookingId, boolean isApproved) {
        Booking booking;
        Item item;
        checkUserExist(userId);
        booking = checkBooking(bookingId);
        checkBookingStatus(booking, isApproved);
        item = booking.getItem();
        checkAccessForApprove(userId, item);
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Override
    public BookingResponseDto getById(long userId, long bookingId) {
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Поиск запроса на бронирование по id возможен только для автора запроса" +
                    " или для владельца вещи!");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    private void checkBookingStatus(Booking booking, boolean isApproved) {
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStateException(String.format("Бронирование id=%d уже находится в статусе %S!",
                    booking.getId(), booking.getStatus()));
        }
        if ((booking.getStatus().equals(BookingStatus.APPROVED) && isApproved) ||
                (booking.getStatus().equals(BookingStatus.REJECTED) && !isApproved)) {
            throw new BookingStateException("Статус уже установлен!");
        }
    }

    private void checkItemOwner(long userId, Item item) {
        if (userId == item.getOwner().getId()) {
            throw new EntityNotFoundException(String.format("Ошибка бронирования! Пользователь id=%d является " +
                    "владельцем вещи id=%d", userId, item.getId()));
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь id=%d не доступна для бронирования!", item.getId()));
        }
    }

    private void checkAccessForApprove(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Ошибка смены статуса запроса на бронирование! Пользователь" +
                    " id=%d не является владельцем вещи id=%d!", userId, item.getId()));
        }
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше даты старта!");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть раньше текущей даты!");
        }
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания не может быть раньше текущей даты!");
        }
    }

    public User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId)));
    }

    public Item checkItem(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    public Booking checkBooking(long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирование с id = %s не найдено!", bookingId)));
    }

    public void checkUserExist(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId));
        }
    }
}
