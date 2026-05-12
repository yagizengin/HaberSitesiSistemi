package HaberSitesiSistemi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import HaberSitesiSistemi.DTO.Request.CommentCreateRequest;
import HaberSitesiSistemi.DTO.Request.CommentUpdateRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.CommentListResponseDTO;
import HaberSitesiSistemi.DTO.Response.CommentResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = commentService.createComment(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CommentResponseDTO>builder()
                .success(true).message("Comment created successfully")
                .data(EntityDtoMapper.toCommentResponseDTO(comment))
                .timestamp(System.currentTimeMillis()).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = commentService.updateComment(id, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<CommentResponseDTO>builder()
                .success(true).message("Comment updated successfully")
                .data(EntityDtoMapper.toCommentResponseDTO(comment))
                .timestamp(System.currentTimeMillis()).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Comment deleted successfully")
                .timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<CommentListResponseDTO>> getArticleComments(
            @PathVariable Long articleId, Pageable pageable) {
        Page<Comment> page = commentService.getApprovedComments(articleId, pageable);
        CommentListResponseDTO data = CommentListResponseDTO.builder()
                .comments(page.getContent().stream().map(EntityDtoMapper::toCommentResponseDTO).toList())
                .pagination(EntityDtoMapper.fromPage(page)).build();
        return ResponseEntity.ok(ApiResponse.<CommentListResponseDTO>builder()
                .success(true).message("Article comments retrieved successfully")
                .data(data).timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> approveComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = commentService.approveComment(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<CommentResponseDTO>builder()
                .success(true).message("Comment approved successfully")
                .data(EntityDtoMapper.toCommentResponseDTO(comment))
                .timestamp(System.currentTimeMillis()).build());
    }
}
