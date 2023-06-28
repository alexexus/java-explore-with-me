package ru.practicum.yandex.compilations;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.compilations.dto.NewCompilationDto;
import ru.practicum.yandex.compilations.dto.UpdateCompilationRequest;
import ru.practicum.yandex.events.EventRepository;
import ru.practicum.yandex.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public List<Compilation> getAllByPinned(Boolean pinned, Integer from, Integer size) {
        QCompilation compilation = QCompilation.compilation;
        List<BooleanExpression> options = new ArrayList<>();

        if (pinned != null) {
            options.add(compilation.pinned.eq(pinned));
            return repository.findAll(options.get(0), PageRequest.of(from / size, size)).toList();
        }

        return repository.findAll(PageRequest.of(from / size, size)).toList();
    }

    @Override
    public Compilation getCompilationById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Compilation not found"));
    }

    @Override
    public Compilation addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(newCompilationDto.getEvents().stream()
                        .map(event -> eventRepository.findById(event)
                                .orElseThrow(() -> new NotFoundException("Event not found")))
                        .collect(Collectors.toList()))
                .build();
        return repository.save(compilation);
    }

    @Override
    public void deleteCompilation(long id) {
        getCompilationById(id);
        repository.deleteById(id);
    }

    @Override
    public Compilation updateCompilation(long id, UpdateCompilationRequest compilation) {
        Compilation oldCompilation = getCompilationById(id);
        if (compilation.getPinned() != null) {
            oldCompilation.setPinned(compilation.getPinned());
        }
        if (compilation.getTitle() != null && !compilation.getTitle().isBlank()) {
            oldCompilation.setTitle(compilation.getTitle());
        }
        if (compilation.getEvents() != null) {
            oldCompilation.setEvents(compilation.getEvents().stream()
                    .map(event -> eventRepository.findById(event)
                            .orElseThrow(() -> new NotFoundException("Event not found")))
                    .collect(Collectors.toList()));
        }
        return repository.save(oldCompilation);
    }
}
