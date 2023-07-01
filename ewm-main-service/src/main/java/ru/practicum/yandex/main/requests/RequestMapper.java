package ru.practicum.yandex.main.requests;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.main.requests.dto.ParticipationRequestDto;

import java.time.format.DateTimeFormatter;

@Component
public class RequestMapper {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(DATE))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
