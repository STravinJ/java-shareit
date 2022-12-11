package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto) {
        checkBookingDate(bookingRequestDto);
        Item item = checkItem(bookingRequestDto.getItemId());
        User booker = checkUser(userId);
        checkItemOwner(userId, item);
        checkItemAvailable(item);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(BookingMapper.toBooking(bookingRequestDto,
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
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
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

    @Override
    public List<BookingResponseDto> getByUser(long userId, String stateIn, Pageable pageRequest) {
        checkUserExist(userId);
        BookingState state;
        try {
            state = BookingState.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + stateIn);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getByOwner(long ownerId, String stateIn, Pageable pageRequest) {
        checkUserExist(ownerId);
        BookingState state;
        try {
            state = BookingState.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + stateIn);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllCurrentByItemsOwnerId(ownerId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastByItemsOwnerId(ownerId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureByItemsOwnerId(ownerId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.WAITING, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.REJECTED, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByItemsOwnerId(ownerId, pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new EndBeforeTodayException();
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new StartBeforeTodayException();
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new EndBeforeStartException();
        }
    }

    public User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
    }

    public Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
    }

    public Booking checkBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(bookingId));
    }

    public void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
