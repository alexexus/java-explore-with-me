package ru.practicum.yandex.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.yandex.categories.dto.CategoryDto;
import ru.practicum.yandex.categories.dto.NewCategoryDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper mapper;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return mapper.toCategoryDto(service.addCategory(mapper.toCategory(newCategoryDto)));
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid NewCategoryDto newCategoryDto,
                                      @PathVariable long catId) {
        return mapper.toCategoryDto(service.updateCategory(mapper.toCategory(newCategoryDto), catId));
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        service.deleteCategory(catId);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        return mapper.toCategoryDto(service.getCategoryById(catId));
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllCategories(from, size).stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
