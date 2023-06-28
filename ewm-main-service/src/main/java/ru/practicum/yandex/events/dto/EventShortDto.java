package ru.practicum.yandex.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.yandex.categories.dto.CategoryDto;
import ru.practicum.yandex.users.dto.UserShortDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
