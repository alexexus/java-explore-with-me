package ru.practicum.yandex.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Boolean pinned = false;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private List<Long> events = new ArrayList<>();
}
