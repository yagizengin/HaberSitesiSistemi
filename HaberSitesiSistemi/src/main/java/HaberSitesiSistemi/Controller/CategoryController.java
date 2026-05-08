package HaberSitesiSistemi.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import HaberSitesiSistemi.DTO.Request.CategoryCreateRequest;
import HaberSitesiSistemi.DTO.Request.CategoryUpdateRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.CategoryResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

        private final CategoryService categoryService;

        @GetMapping
        public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
                List<Category> categories = categoryService.getAllCategories();
                List<CategoryResponseDTO> data = categories.stream()
                                .map(EntityDtoMapper::toCategoryResponseDTO).toList();
                return ResponseEntity.ok(ApiResponse.<List<CategoryResponseDTO>>builder()
                                .success(true).message("Categories retrieved successfully")
                                .data(data).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Long id) {
                Category category = categoryService.getCategoryById(id);
                return ResponseEntity.ok(ApiResponse.<CategoryResponseDTO>builder()
                                .success(true).message("Category retrieved successfully")
                                .data(EntityDtoMapper.toCategoryResponseDTO(category))
                                .timestamp(System.currentTimeMillis()).build());
        }

        @PostMapping
        public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
                        @Valid @RequestBody CategoryCreateRequest request) {
                Category category = categoryService.createCategory(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponseDTO>builder()
                                .success(true).message("Category created successfully")
                                .data(EntityDtoMapper.toCategoryResponseDTO(category))
                                .timestamp(System.currentTimeMillis()).build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
                        @PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request) {
                Category category = categoryService.updateCategory(id, request);
                return ResponseEntity.ok(ApiResponse.<CategoryResponseDTO>builder()
                                .success(true).message("Category updated successfully")
                                .data(EntityDtoMapper.toCategoryResponseDTO(category))
                                .timestamp(System.currentTimeMillis()).build());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
                categoryService.deleteCategory(id);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true).message("Category deactivated successfully")
                                .timestamp(System.currentTimeMillis()).build());
        }
}
