package ru.practicum.yandex.main.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.main.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public Category addCategory(Category category) {
        return repository.save(category);
    }

    @Override
    public void deleteCategory(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Category not found");
        }
    }

    @Override
    public Category getCategoryById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public Category updateCategory(Category category, long id) {
        Category oldCategory = getCategoryById(id);
        if (category.getName() != null && !category.getName().isBlank()) {
            oldCategory.setName(category.getName());
        }
        return repository.save(oldCategory);
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {
        return repository.findAll(PageRequest.of(from / size, size)).toList();
    }
}
