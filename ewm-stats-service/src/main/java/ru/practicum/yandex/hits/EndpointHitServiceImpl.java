package ru.practicum.yandex.hits;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EndpointHitRepository repository;

    @Override
    public EndpointHit addEndpointHit(EndpointHit endpointHit) {
        endpointHit.setCreated(LocalDateTime.now());
        return repository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(List<String> uris, String start, String end, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getAllStatsDistinctIp(LocalDateTime.parse(start, DATE), LocalDateTime.parse(end, DATE));
            } else {
                return repository.getAllStats(LocalDateTime.parse(start, DATE), LocalDateTime.parse(end, DATE));
            }
        } else {
            if (unique) {
                return repository.getStatsByUrisDistinctIp(LocalDateTime.parse(start, DATE), LocalDateTime.parse(end, DATE), uris);
            } else {
                return repository.getStatsByUris(LocalDateTime.parse(start, DATE), LocalDateTime.parse(end, DATE), uris);
            }
        }
    }
}
