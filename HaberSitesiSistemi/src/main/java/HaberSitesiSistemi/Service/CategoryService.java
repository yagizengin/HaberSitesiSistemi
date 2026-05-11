package HaberSitesiSistemi.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.CategoryCreateRequest;
import HaberSitesiSistemi.DTO.Request.CategoryUpdateRequest;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Repository.CategoryRepository;
import HaberSitesiSistemi.Util.HtmlSanitizer;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryCreateRequest request) {
        log.info("Creating category with name: '{}'", request.getName());

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            Optional<Category> existingOpt = categoryRepository.findByNameIgnoreCase(request.getName());
            if (existingOpt.isPresent()) {
                Category existing = existingOpt.get();
                if (!existing.isActive()) {
                    log.info("Reactivating existing inactive category ID: {}", existing.getCategoryId());
                    existing.setActive(true);
                    existing.setDescription(HtmlSanitizer.sanitize(request.getDescription()));
                    Category reactivated = categoryRepository.save(existing);
                    log.info("Category reactivated successfully with ID: {}", reactivated.getCategoryId());
                    return reactivated;
                }
            }
            log.warn("Category creation failed: Name '{}' already exists", request.getName());
            throw new ConflictException("Category name already exists");
        }

        Category category = new Category();
        category.setName(HtmlSanitizer.sanitize(request.getName()));
        category.setDescription(HtmlSanitizer.sanitize(request.getDescription()));

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getCategoryId());
        return savedCategory;
    }

    public Category updateCategory(Long categoryId, CategoryUpdateRequest request) {
        log.info("Updating category ID: {}", categoryId);

        Category category = getCategoryEntityById(categoryId);

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            log.warn("Category update failed: Name '{}' already exists", request.getName());
            throw new ConflictException("Category name already exists");
        }

        category.setName(HtmlSanitizer.sanitize(request.getName()));
        category.setDescription(HtmlSanitizer.sanitize(request.getDescription()));

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category {} updated successfully", categoryId);
        return updatedCategory;
    }

    public Category deleteCategory(Long categoryId) {
        log.info("Deactivating category ID: {}", categoryId);

        Category category = getCategoryEntityById(categoryId);

        if (!category.isActive()) {
            log.warn("Category {} is already deactivated", categoryId);
            throw new ConflictException("Category is already deactivated");
        }

        category.setActive(false);

        Category deactivatedCategory = categoryRepository.save(category);
        log.info("Category {} deactivated successfully", categoryId);
        return deactivatedCategory;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching all active categories");
        return categoryRepository.findByActive(true);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        return getCategoryEntityById(categoryId);
    }

    private Category getCategoryEntityById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", categoryId);
                    return new ResourceNotFoundException("Category", "id", categoryId);
                });
    }

    @Transactional(readOnly = true)
    public long countAllCategories() {
        log.info("Counting total active categories");
        return categoryRepository.countByActive(true);
    }
}
