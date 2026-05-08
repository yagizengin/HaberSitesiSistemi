package HaberSitesiSistemi.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import HaberSitesiSistemi.DTO.Request.UserUpdateRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.ArticleListResponseDTO;
import HaberSitesiSistemi.DTO.Response.CommentListResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserDetailResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CommentService;
import HaberSitesiSistemi.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers(Pageable pageable) {
        Page<User> page = userService.getAllUsers(pageable);
        List<UserResponseDTO> users = page.getContent().stream()
                .map(EntityDtoMapper::toUserResponseDTO)
                .toList();

        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponseDTO>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDetailResponseDTO data = EntityDtoMapper.toUserDetailResponseDTO(user);

        return ResponseEntity.ok(ApiResponse.<UserDetailResponseDTO>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<UserDetailResponseDTO>> getCurrentUser(
            @org.springframework.web.bind.annotation.RequestParam Long userId) {
        User user = userService.getUserById(userId);
        UserDetailResponseDTO data = EntityDtoMapper.toUserDetailResponseDTO(user);

        return ResponseEntity.ok(ApiResponse.<UserDetailResponseDTO>builder()
                .success(true)
                .message("Current user profile retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        User user = userService.updateProfile(id, request);
        UserResponseDTO data = EntityDtoMapper.toUserResponseDTO(user);

        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User account deactivated successfully")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getUserArticles(
            @PathVariable Long id,
            Pageable pageable) {

        Page<Article> page = articleService.getArticlesByAuthor(id, pageable);
        ArticleListResponseDTO data = EntityDtoMapper.toArticleListResponseDTO(page);

        return ResponseEntity.ok(ApiResponse.<ArticleListResponseDTO>builder()
                .success(true)
                .message("User articles retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentListResponseDTO>> getUserComments(
            @PathVariable Long id,
            Pageable pageable) {

        Page<Comment> page = commentService.getCommentsByUser(id, pageable);

        CommentListResponseDTO data = CommentListResponseDTO.builder()
                .comments(page.getContent().stream()
                        .map(EntityDtoMapper::toCommentResponseDTO)
                        .toList())
                .pagination(EntityDtoMapper.fromPage(page))
                .build();

        return ResponseEntity.ok(ApiResponse.<CommentListResponseDTO>builder()
                .success(true)
                .message("User comments retrieved successfully")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
