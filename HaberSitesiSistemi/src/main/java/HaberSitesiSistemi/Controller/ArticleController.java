package HaberSitesiSistemi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import HaberSitesiSistemi.DTO.Request.ArticleCreateRequest;
import HaberSitesiSistemi.DTO.Request.ArticleUpdateRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.ArticleDetailResponseDTO;
import HaberSitesiSistemi.DTO.Response.ArticleListResponseDTO;
import HaberSitesiSistemi.DTO.Response.ArticleResponseDTO;
import HaberSitesiSistemi.DTO.Response.CommentListResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getAllArticles(Pageable pageable) {
        Page<Article> page = articleService.getAllArticles(pageable);
        ArticleListResponseDTO data = EntityDtoMapper.toArticleListResponseDTO(page);

        return ResponseEntity.ok(ApiResponse.<ArticleListResponseDTO>builder()
                .success(true)
                .message("Articles retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailResponseDTO>> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        ArticleDetailResponseDTO data = EntityDtoMapper.toArticleDetailResponseDTO(article);

        return ResponseEntity.ok(ApiResponse.<ArticleDetailResponseDTO>builder()
                .success(true)
                .message("Article retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponseDTO>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Article article = articleService.createArticle(request, userDetails.getUserId());
        ArticleResponseDTO data = EntityDtoMapper.toArticleResponseDTO(article);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ArticleResponseDTO>builder()
                        .success(true)
                        .message("Article created successfully")
                        .data(data)
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponseDTO>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Article article = articleService.updateArticle(id, request, userDetails.getUserId());
        ArticleResponseDTO data = EntityDtoMapper.toArticleResponseDTO(article);

        return ResponseEntity.ok(ApiResponse.<ArticleResponseDTO>builder()
                .success(true)
                .message("Article updated successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        articleService.deleteArticle(id, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Article deleted successfully")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ArticleResponseDTO>> publishArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Article article = articleService.publishArticle(id, userDetails.getUserId());
        ArticleResponseDTO data = EntityDtoMapper.toArticleResponseDTO(article);

        return ResponseEntity.ok(ApiResponse.<ArticleResponseDTO>builder()
                .success(true)
                .message("Article published successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/category/{catId}")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getArticlesByCategory(
            @PathVariable Long catId,
            Pageable pageable) {

        Page<Article> page = articleService.getArticlesByCategory(catId, pageable);
        ArticleListResponseDTO data = EntityDtoMapper.toArticleListResponseDTO(page);

        return ResponseEntity.ok(ApiResponse.<ArticleListResponseDTO>builder()
                .success(true)
                .message("Articles by category retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getArticlesByTag(
            @PathVariable Long tagId,
            Pageable pageable) {

        Page<Article> page = articleService.getArticlesByTag(tagId, pageable);
        ArticleListResponseDTO data = EntityDtoMapper.toArticleListResponseDTO(page);

        return ResponseEntity.ok(ApiResponse.<ArticleListResponseDTO>builder()
                .success(true)
                .message("Articles by tag retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> searchArticles(
            @RequestParam String query,
            Pageable pageable) {

        Page<Article> page = articleService.searchArticles(query, pageable);
        ArticleListResponseDTO data = EntityDtoMapper.toArticleListResponseDTO(page);

        return ResponseEntity.ok(ApiResponse.<ArticleListResponseDTO>builder()
                .success(true)
                .message("Search results retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentListResponseDTO>> getArticleComments(
            @PathVariable Long id,
            Pageable pageable) {

        Page<Comment> page = commentService.getApprovedComments(id, pageable);

        CommentListResponseDTO data = CommentListResponseDTO.builder()
                .comments(page.getContent().stream()
                        .map(EntityDtoMapper::toCommentResponseDTO)
                        .toList())
                .pagination(EntityDtoMapper.fromPage(page))
                .build();

        return ResponseEntity.ok(ApiResponse.<CommentListResponseDTO>builder()
                .success(true)
                .message("Article comments retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
