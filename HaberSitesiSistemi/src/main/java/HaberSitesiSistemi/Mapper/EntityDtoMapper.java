package HaberSitesiSistemi.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import HaberSitesiSistemi.DTO.Request.ArticleCreateRequest;
import HaberSitesiSistemi.DTO.Request.ArticleUpdateRequest;
import HaberSitesiSistemi.DTO.Request.CategoryCreateRequest;
import HaberSitesiSistemi.DTO.Request.CategoryUpdateRequest;
import HaberSitesiSistemi.DTO.Request.CommentCreateRequest;
import HaberSitesiSistemi.DTO.Request.CommentUpdateRequest;
import HaberSitesiSistemi.DTO.Request.MediaUploadRequest;
import HaberSitesiSistemi.DTO.Request.TagCreateRequest;
import HaberSitesiSistemi.DTO.Request.UserRegisterRequest;
import HaberSitesiSistemi.DTO.Request.UserUpdateRequest;
import HaberSitesiSistemi.DTO.Response.ArticleDetailResponseDTO;
import HaberSitesiSistemi.DTO.Response.ArticleResponseDTO;
import HaberSitesiSistemi.DTO.Response.ArticleSummaryDTO;
import HaberSitesiSistemi.DTO.Response.CategoryResponseDTO;
import HaberSitesiSistemi.DTO.Response.CategoryWithArticlesDTO;
import HaberSitesiSistemi.DTO.Response.CommentResponseDTO;
import HaberSitesiSistemi.DTO.Response.LoginResponseDTO;
import HaberSitesiSistemi.DTO.Response.MediaResponseDTO;
import HaberSitesiSistemi.DTO.Response.PaginationDTO;
import HaberSitesiSistemi.DTO.Response.ArticleListResponseDTO;
import HaberSitesiSistemi.DTO.Response.SavedArticleResponseDTO;
import HaberSitesiSistemi.DTO.Response.TagResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserDetailResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserProfileResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserResponseDTO;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.Media;
import HaberSitesiSistemi.Model.Role;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Model.Tag;
import HaberSitesiSistemi.Model.User;

public final class EntityDtoMapper {

    private static final int ARTICLE_EXCERPT_LENGTH = 160;

    private EntityDtoMapper() {
    }

    public static User toUserEntity(UserRegisterRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return user;
    }

    public static void updateUserEntity(User user, UserUpdateRequest request) {
        if (user == null || request == null) {
            return;
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
    }

    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(mapRoleNames(user.getRoles()))
                .build();
    }

    public static UserDetailResponseDTO toUserDetailResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDetailResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(mapRoleNames(user.getRoles()))
                .articleCount(sizeOf(user.getArticles()))
                .commentCount(sizeOf(user.getComments()))
                .savedArticleCount(0L)
                .articles(mapArticleSummaries(user.getArticles()))
                .comments(mapComments(user.getComments()))
                .build();
    }

    public static UserProfileResponseDTO toUserProfileResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .roles(mapRoleNames(user.getRoles()))
                .recentArticles(mapArticleSummaries(user.getArticles()))
                .recentComments(mapComments(user.getComments()))
                .build();
    }

    public static Category toCategoryEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }

    public static void updateCategoryEntity(Category category, CategoryUpdateRequest request) {
        if (category == null || request == null) {
            return;
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }

    public static CategoryResponseDTO toCategoryResponseDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .articleCount(sizeOf(category.getArticles()))
                .build();
    }

    public static CategoryWithArticlesDTO toCategoryWithArticlesDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryWithArticlesDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .articleCount(sizeOf(category.getArticles()))
                .articles(mapArticleSummaries(category.getArticles()))
                .build();
    }

    public static Tag toTagEntity(TagCreateRequest request) {
        if (request == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        return tag;
    }

    public static TagResponseDTO toTagResponseDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        return TagResponseDTO.builder()
                .tagId(tag.getTagId())
                .name(tag.getName())
                .articleCount(sizeOf(tag.getArticles()))
                .build();
    }

    public static Media toMediaEntity(MediaUploadRequest request) {
        if (request == null) {
            return null;
        }

        Media media = new Media();
        media.setFileType(request.getFileType());
        return media;
    }

    public static MediaResponseDTO toMediaResponseDTO(Media media) {
        if (media == null) {
            return null;
        }

        return MediaResponseDTO.builder()
                .mediaId(media.getMediaId())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .uploadedAt(media.getUploadedAt())
                .articleId(media.getArticle() != null ? media.getArticle().getArticleId() : null)
                .build();
    }

    public static Comment toCommentEntity(CommentCreateRequest request) {
        if (request == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        return comment;
    }

    public static void updateCommentEntity(Comment comment, CommentUpdateRequest request) {
        if (comment == null || request == null) {
            return;
        }

        comment.setContent(request.getContent());
    }

    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .approved(comment.isApproved())
                .articleId(comment.getArticle() != null ? comment.getArticle().getArticleId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getUserId() : null)
                .username(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .build();
    }

    public static Article toArticleEntity(ArticleCreateRequest request) {
        if (request == null) {
            return null;
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        return article;
    }

    public static void updateArticleEntity(Article article, ArticleUpdateRequest request) {
        if (article == null || request == null) {
            return;
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
    }

    public static ArticleResponseDTO toArticleResponseDTO(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleResponseDTO.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .content(article.getContent())
                .published(article.isPublished())
                .viewCount(article.getViewCount())
                .publishedAt(article.getPublishedAt())
                .categoryId(article.getCategory() != null ? article.getCategory().getCategoryId() : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .authorId(article.getAuthor() != null ? article.getAuthor().getUserId() : null)
                .authorUsername(article.getAuthor() != null ? article.getAuthor().getUsername() : null)
                .build();
    }

    public static ArticleSummaryDTO toArticleSummaryDTO(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleSummaryDTO.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .excerpt(buildExcerpt(article.getContent()))
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .authorUsername(article.getAuthor() != null ? article.getAuthor().getUsername() : null)
                .publishedAt(article.getPublishedAt())
                .viewCount(article.getViewCount())
                .firstMediaUrl(firstMediaUrl(article.getMediaFiles()))
                .build();
    }

    public static ArticleDetailResponseDTO toArticleDetailResponseDTO(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleDetailResponseDTO.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .content(article.getContent())
                .published(article.isPublished())
                .viewCount(article.getViewCount())
                .publishedAt(article.getPublishedAt())
                .category(toCategoryResponseDTO(article.getCategory()))
                .author(toUserResponseDTO(article.getAuthor()))
                .tags(mapTags(article.getTags()))
                .comments(mapComments(article.getComments()))
                .media(mapMedia(article.getMediaFiles()))
                .build();
    }

    public static SavedArticleResponseDTO toSavedArticleResponseDTO(SavedArticle savedArticle) {
        if (savedArticle == null) {
            return null;
        }

        return SavedArticleResponseDTO.builder()
                .saveId(savedArticle.getSaveId())
                .savedAt(savedArticle.getSavedAt())
                .userId(savedArticle.getUser() != null ? savedArticle.getUser().getUserId() : null)
                .articleId(savedArticle.getArticle() != null ? savedArticle.getArticle().getArticleId() : null)
                .article(toArticleSummaryDTO(savedArticle.getArticle()))
                .build();
    }

    public static LoginResponseDTO toLoginResponseDTO(String token, long expiresIn, User user) {
        if (user == null) {
            return null;
        }

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(user.getUserId())
                .username(user.getUsername())
                .roles(mapRoleNames(user.getRoles()))
                .build();
    }

    public static List<ArticleSummaryDTO> mapArticleSummaries(Set<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return Collections.emptyList();
        }

        return articles.stream()
                .filter(Objects::nonNull)
                .map(EntityDtoMapper::toArticleSummaryDTO)
                .collect(Collectors.toList());
    }

    public static List<CommentResponseDTO> mapComments(Set<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .filter(Objects::nonNull)
                .map(EntityDtoMapper::toCommentResponseDTO)
                .collect(Collectors.toList());
    }

    public static List<MediaResponseDTO> mapMedia(Set<Media> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) {
            return Collections.emptyList();
        }

        return mediaFiles.stream()
                .filter(Objects::nonNull)
                .map(EntityDtoMapper::toMediaResponseDTO)
                .collect(Collectors.toList());
    }

    public static List<TagResponseDTO> mapTags(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        return tags.stream()
                .filter(Objects::nonNull)
                .map(EntityDtoMapper::toTagResponseDTO)
                .collect(Collectors.toList());
    }

    public static Set<String> mapRoleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public static String buildExcerpt(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        if (content.length() <= ARTICLE_EXCERPT_LENGTH) {
            return content;
        }

        return content.substring(0, ARTICLE_EXCERPT_LENGTH).trim() + "...";
    }

    public static String firstMediaUrl(Set<Media> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) {
            return null;
        }

        return mediaFiles.stream()
                .filter(Objects::nonNull)
                .map(Media::getFileUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static long sizeOf(Set<?> values) {
        return values == null ? 0L : values.size();
    }

    public static PaginationDTO fromPage(org.springframework.data.domain.Page<?> page) {
        if (page == null) {
            return null;
        }

        return PaginationDTO.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public static ArticleListResponseDTO toArticleListResponseDTO(org.springframework.data.domain.Page<Article> page) {
        if (page == null) {
            return null;
        }

        List<ArticleSummaryDTO> articles = page.getContent().stream()
                .filter(Objects::nonNull)
                .map(EntityDtoMapper::toArticleSummaryDTO)
                .collect(Collectors.toList());

        return ArticleListResponseDTO.builder()
                .articles(articles)
                .pagination(fromPage(page))
                .build();
    }
}