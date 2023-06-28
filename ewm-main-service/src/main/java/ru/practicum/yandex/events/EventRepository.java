package ru.practicum.yandex.events;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>,
        QuerydslPredicateExecutor<Event> {

    List<Event> findByInitiatorId(long id, Pageable pageable);

    Event findByIdAndInitiatorId(long id, long initiatorId);
}
