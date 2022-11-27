package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestService {

    ItemRequestOutDto add(long userId, ItemRequestInDto requestInDto);

    List<ItemRequestOutDto> getByOwner(long userId);

    List<ItemRequestOutDto> getAll(long userId, Pageable pageRequest);

    ItemRequestOutDto getById(long userId, long requestId);

}
