package ru.practicum.yandex.compilations;

import ru.practicum.yandex.compilations.dto.NewCompilationDto;
import ru.practicum.yandex.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<Compilation> getAllByPinned(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(long id);

    Compilation addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long id);

    Compilation updateCompilation(long id, UpdateCompilationRequest updateCompilationRequest);
}
