package ru.practicum.yandex.main.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.main.events.Event;
import ru.practicum.yandex.main.requests.RequestRepository;
import ru.practicum.yandex.stats.client.HitClient;
import ru.practicum.yandex.stats.dto.EndpointHitDto;
import ru.practicum.yandex.stats.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper mapper = new ObjectMapper();
    private final RequestRepository requestRepository;
    private final HitClient hitClient;
    @Value(value = "${app.name}")
    private String appName;

    public void addHit(HttpServletRequest request) {
        hitClient.addHit(EndpointHitDto.builder()
                .app(appName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .build());
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        ResponseEntity<Object> response = hitClient.getStats(start, end, uris, unique);

        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStats[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<Event> publishedEvents = getPublished(events);

        if (events.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minTime = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minTime.isPresent()) {
            List<String> uris = publishedEvents.stream()
                    .map(Event::getId)
                    .map(id -> ("/events/" + id))
                    .collect(Collectors.toList());
            List<ViewStats> stats = getStats(
                    minTime.get().format(DATE),
                    LocalDateTime.now().plusDays(1L).format(DATE),
                    uris,
                    true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }
        return views;
    }

    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> ids = getPublished(events).stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> eventsRequests = new HashMap<>();

        if (!ids.isEmpty()) {
            requestRepository.getEventsRequests(ids)
                    .forEach(er -> eventsRequests.put(er.getEventId(), er.getCount()));
        }

        return eventsRequests;
    }

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }
}
