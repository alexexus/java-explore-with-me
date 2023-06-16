package hits;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository repository;

    @Override
    public EndpointHit addEndpointHit(EndpointHit endpointHit) {
        endpointHit.setCreated(LocalDateTime.now());
        return repository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique) {
        if (uris == null) {
            return repository.getStatsWithoutUris(start, end);
        }
        if (unique) {
            return repository.getUniqueStats(uris, start, end);
        }
        return repository.getStats(uris, start, end);
    }
}
