package ru.practicum.yandex.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.yandex.events.location.Location;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotBlank
    private String eventDate;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}
