package ru.practicum.yandex.categories;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.categories.dto.CategoryDto;
import ru.practicum.yandex.categories.dto.NewCategoryDto;

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
