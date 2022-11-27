package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    User user = new User(
            1L,
            "userName",
            "user@email.com"
    );

    @MockBean
    UserStorage userStorage;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userStorage = mock(UserStorage.class);
        userService = new UserServiceImpl(userStorage);
    }

    @Test
    void getAll() {
        when(userStorage.findAll())
                .thenReturn(Collections.singletonList(user));
        List<UserDto> userDtos = userService.getAll();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        assertThat(userDtos, equalTo(List.of(UserMapper.toUserDto(user))));
    }

    @Test
    void add() {
        when(userStorage.save(any()))
                .thenReturn(user);
        UserDto userDto = userService.add(UserMapper.toUserDto(user));

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void update() {
        when(userStorage.save(any()))
                .thenReturn(user);
        when(userStorage.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.update(UserMapper.toUserDto(user));

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getById() {
        when(userStorage.save(any()))
                .thenReturn(user);
        when(userStorage.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getById(user.getId());

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getByIdWrongId() {
        when(userStorage.save(any()))
                .thenReturn(user);
        when(userStorage.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(userStorage.findAll()).thenReturn(Collections.emptyList());
        userService.delete(user.getId());
        assertEquals(0, userStorage.findAll().size());

    }

}