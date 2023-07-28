package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.dto.FeedEventDTO;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.dto.UsersDTO;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);

    @GetMapping
    public List<UsersDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(mapper::userToDto)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public UsersDTO getUser(@PathVariable int id) {
        return mapper.userToDto(userService.getUser(id));
    }

    @PostMapping
    public UsersDTO addUser(@RequestBody @Validated UsersDTO usersDTO) {
        return mapper.userToDto(userService.addUser(mapper.dtoToUser(usersDTO)));
    }

    @PutMapping
    public UsersDTO updateUser(@RequestBody @Validated UsersDTO usersDTO) {
        return mapper.userToDto(userService.updateUser(mapper.dtoToUser(usersDTO)));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UsersDTO> getFriends(@PathVariable int id) {
        return userService.getFriends(id)
                .stream()
                .map(mapper::userToDto)
                .collect(toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UsersDTO> getFriendsCommon(@PathVariable int id, @PathVariable int otherId) {
        return userService.getFriendsCommon(id, otherId)
                .stream()
                .map(mapper::userToDto)
                .collect(toList());
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDTO> getRecommendations(@PathVariable int id) {
        return userService.getRecommendations(id)
                .stream()
                .map(mapper::filmToDto)
                .collect(toList());
    }

    @GetMapping("/{id}/feed")
    public List<FeedEventDTO> getFeed(@PathVariable int id) {
        return userService.getFeed(id)
                .stream()
                .map(mapper::feedEventToDto)
                .collect(toList());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}
