package hits;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    EndpointHit addEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique);
}
