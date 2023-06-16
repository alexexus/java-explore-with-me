package hits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new hits.ViewStats(app, uri, count(distinct ip)) from EndpointHit " +
            "where (uri in :uris) and (created between :start and :end) " +
            "group by app, uri order by count(distinct ip) desc")
    List<ViewStats> getUniqueStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new hits.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where (uri in :uris) and (created between :start and :end) " +
            "group by app, uri order by count(ip) desc")
    List<ViewStats> getStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new hits.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where created between :start and :end group by app, uri order by count(ip) desc")
    List<ViewStats> getStatsWithoutUris(LocalDateTime start, LocalDateTime end);
}
