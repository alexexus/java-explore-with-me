package ru.practicum.yandex.main.users;

import java.util.List;

public interface UserService {

    User addUser(User user);

    void deleteUser(long id);

    List<User> getAllUsers(List<Long> ids,Integer from, Integer size);
}
