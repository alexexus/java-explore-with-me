package ru.practicum.yandex.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
        QuerydslPredicateExecutor<User> {
}
