package ru.practicum.yandex.main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.yandex.main.categories.dto.CategoryDto;
import ru.practicum.yandex.main.users.dto.UserShortDto;

import java.time.LocalDateTime;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
