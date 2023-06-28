package ru.practicum.yandex.hits;

import java.util.List;

public interface EndpointHitService {

    EndpointHit addEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getStats(List<String> uris, String start, String end, boolean unique);
}
