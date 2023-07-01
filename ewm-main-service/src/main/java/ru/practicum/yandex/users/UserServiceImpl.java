package ru.practicum.yandex.users;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User addUser(User user) {
        return repository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers(List<Long> ids, Integer from, Integer size) {
        if (ids != null) {
            QUser user = QUser.user;
            List<BooleanExpression> options = new ArrayList<>();
            options.add(user.id.in(ids));
            return repository.findAll(options.get(0), PageRequest.of(from / size, size)).toList();
        }

        return repository.findAll(PageRequest.of(from / size, size)).toList();
    }
}
