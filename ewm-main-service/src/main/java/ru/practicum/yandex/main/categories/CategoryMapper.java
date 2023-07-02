package ru.practicum.yandex.main.categories;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.main.categories.dto.NewCategoryDto;
import ru.practicum.yandex.main.categories.dto.CategoryDto;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}
