package ru.practicum.yandex.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.yandex.categories.dto.CategoryDto;
import ru.practicum.yandex.events.State;
import ru.practicum.yandex.events.location.Location;
import ru.practicum.yandex.users.dto.UserShortDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private long id;

    private String annotation;

    private CategoryDto category;

    private long confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Long views;
}
