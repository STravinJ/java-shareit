package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.Validator;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping()
    public BookingResponseDto add(@RequestHeader(HEADER_USER_ID) long userId,
                                  @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.add(userId, bookingRequestDto);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam("approved") boolean isApproved) {
        return bookingService.approve(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getById(@RequestHeader(HEADER_USER_ID) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getByUser(@RequestHeader(HEADER_USER_ID) long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL") String state,
                                              @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        Validator.fromPageValidation(from);
        int page = from / size;
        return bookingService.getByUser(userId, state, PageRequest.of(page, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(@RequestHeader(HEADER_USER_ID) long userId,
                                               @RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               Integer from,
                                               @RequestParam(name = "size", defaultValue = "10")
                                               Integer size) {
        Validator.fromPageValidation(from);
        int page = from / size;
        return bookingService.getByOwner(userId, state, PageRequest.of(page, size));
    }

}
