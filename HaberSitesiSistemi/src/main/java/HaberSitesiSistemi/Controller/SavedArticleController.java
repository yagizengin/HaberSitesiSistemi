package HaberSitesiSistemi.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.SavedArticleResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Service.SavedArticleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saved-articles")
@RequiredArgsConstructor
public class SavedArticleController {

    private final SavedArticleService savedArticleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedArticleResponseDTO>>> getUserSavedArticles(
            @RequestParam Long userId, Pageable pageable) {
        Page<SavedArticle> page = savedArticleService.getUserSavedArticles(userId, pageable);
        List<SavedArticleResponseDTO> data = page.getContent().stream()
                .map(EntityDtoMapper::toSavedArticleResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.<List<SavedArticleResponseDTO>>builder()
                .success(true).message("Saved articles retrieved successfully")
                .data(data).timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/{articleId}")
    public ResponseEntity<ApiResponse<SavedArticleResponseDTO>> saveArticle(
            @PathVariable Long articleId, @RequestParam Long userId) {
        SavedArticle saved = savedArticleService.saveArticle(userId, articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SavedArticleResponseDTO>builder()
                .success(true).message("Article saved successfully")
                .data(EntityDtoMapper.toSavedArticleResponseDTO(saved))
                .timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Void>> unsaveArticle(
            @PathVariable Long articleId, @RequestParam Long userId) {
        savedArticleService.unsaveArticle(userId, articleId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Article unsaved successfully")
                .timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/{articleId}/saved")
    public ResponseEntity<ApiResponse<Boolean>> isArticleSaved(
            @PathVariable Long articleId, @RequestParam Long userId) {
        boolean isSaved = savedArticleService.isArticleSaved(userId, articleId);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true).message("Save status checked successfully")
                .data(isSaved).timestamp(System.currentTimeMillis()).build());
    }
}
