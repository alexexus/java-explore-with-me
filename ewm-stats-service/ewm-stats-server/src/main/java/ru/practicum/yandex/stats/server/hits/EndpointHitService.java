package ru.practicum.yandex.stats.server.hits;

import ru.practicum.yandex.stats.dto.ViewStats;

import java.util.List;

public interface EndpointHitService {

    EndpointHit addEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getStats(List<String> uris, String start, String end, boolean unique);
}
