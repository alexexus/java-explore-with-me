package ru.practicum.yandex.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.events.Event;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final HitClient service;
    private final ObjectMapper mapper = new ObjectMapper();


    public void addHit(HttpServletRequest request) {
        service.addHit(EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .build());
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        ResponseEntity<Object> response = service.getStats(start, end, uris, unique);

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

        List<String> uris = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        List<ViewStats> stats = getStats(
                "2020-05-05 00:00:00",
                LocalDateTime.now().plusDays(1L).format(DATE),
                uris,
                true);
        stats.forEach(stat -> {
            Long eventId = Long.parseLong(stat.getUri()
                    .split("/", 0)[2]);
            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
        });

        return views;
    }

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }
}
