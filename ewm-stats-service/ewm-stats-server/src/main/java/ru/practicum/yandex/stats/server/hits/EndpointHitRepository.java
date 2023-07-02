package ru.practicum.yandex.stats.server.hits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.yandex.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.yandex.stats.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.created BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getAllStatsDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.yandex.stats.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.created BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.yandex.stats.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.created BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsByUrisDistinctIp(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("SELECT new ru.practicum.yandex.stats.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.created BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uri);
}
