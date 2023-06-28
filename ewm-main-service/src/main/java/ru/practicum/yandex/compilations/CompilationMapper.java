package ru.practicum.yandex.compilations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.yandex.compilations.dto.CompilationDto;
import ru.practicum.yandex.events.EventMapper;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
