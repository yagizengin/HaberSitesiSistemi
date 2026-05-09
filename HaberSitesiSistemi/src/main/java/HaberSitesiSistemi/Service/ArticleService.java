package HaberSitesiSistemi.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.ArticleCreateRequest;
import HaberSitesiSistemi.DTO.Request.ArticleUpdateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Model.Tag;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.ArticleRepository;
import HaberSitesiSistemi.Repository.CategoryRepository;
import HaberSitesiSistemi.Repository.TagRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import HaberSitesiSistemi.Util.HtmlSanitizer;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ForbiddenException;
import HaberSitesiSistemi.Exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import HaberSitesiSistemi.Model.Media;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService;

    public Article createArticle(ArticleCreateRequest request, Long authorId) {
        log.info("Creating article with title: '{}' by author ID: {}", request.getTitle(), authorId);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Article creation failed: Author not found with ID: {}", authorId);
                    return new ResourceNotFoundException("User", "id", authorId);
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Article creation failed: Category not found with ID: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                });

        Article article = new Article();
        article.setTitle(HtmlSanitizer.sanitize(request.getTitle()));
        article.setContent(HtmlSanitizer.sanitizeHtml(request.getContent()));
        article.setAuthor(author);
        article.setCategory(category);

        if (request.getCoverImageId() != null) {
            Media coverMedia = mediaService.getMediaById(request.getCoverImageId());
            article.setCoverImage(coverMedia);
            coverMedia.setArticle(article);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tags::add);
            }
            article.setTags(tags);
        }

        Article savedArticle = articleRepository.save(article);
        if (request.getCoverImageId() != null) {
            mediaService.addMediaToArticle(request.getCoverImageId(), savedArticle.getArticleId());
        }
        associateEmbeddedImages(savedArticle);
        log.info("Article created successfully with ID: {}", savedArticle.getArticleId());
        return savedArticle;
    }

    public Article updateArticle(Long articleId, ArticleUpdateRequest request, Long userId) {
        log.info("Updating article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        User actionUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        boolean isAdmin = actionUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("ROLE_ADMIN"));

        if (!article.getAuthor().getUserId().equals(userId) && !isAdmin) {
            log.warn("Update denied: User {} is not the author of article {} and is not admin", userId, articleId);
            throw new ForbiddenException("You are not authorized to update this article");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Article update failed: Category not found with ID: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                });

        article.setTitle(HtmlSanitizer.sanitize(request.getTitle()));
        article.setContent(HtmlSanitizer.sanitizeHtml(request.getContent()));
        article.setCategory(category);

        if (request.getCoverImageId() != null) {
            Media coverMedia = mediaService.getMediaById(request.getCoverImageId());
            article.setCoverImage(coverMedia);
            coverMedia.setArticle(article);
        } else {
            article.setCoverImage(null);
        }

        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tags::add);
            }
            article.setTags(tags);
        }

        Article updatedArticle = articleRepository.save(article);
        if (request.getCoverImageId() != null) {
            mediaService.addMediaToArticle(request.getCoverImageId(), updatedArticle.getArticleId());
        }
        associateEmbeddedImages(updatedArticle);
        log.info("Article {} updated successfully", articleId);
        return updatedArticle;
    }

    public void deleteArticle(Long articleId, Long userId) {
        log.info("Deleting article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        User actionUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        boolean isAdmin = actionUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("ROLE_ADMIN"));

        if (!article.getAuthor().getUserId().equals(userId) && !isAdmin) {
            log.warn("Delete denied: User {} is not the author of article {} and is not admin", userId, articleId);
            throw new ForbiddenException("You are not authorized to delete this article");
        }
        mediaService.deleteMediaByArticleId(articleId);
        articleRepository.delete(article);
        log.info("Article {} deleted successfully", articleId);
    }

    public Article publishArticle(Long articleId, Long userId) {
        log.info("Publishing article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        if (article.isPublished()) {
            log.warn("Article {} is already published", articleId);
            throw new ConflictException("Article is already published");
        }

        article.setPublished(true);
        article.setPublishedAt(LocalDateTime.now());

        Article publishedArticle = articleRepository.save(article);
        log.info("Article {} published successfully", articleId);
        return publishedArticle;
    }

    public Article getArticleById(Long articleId) {
        log.info("Fetching article with ID: {}", articleId);

        Article article = getArticleEntityById(articleId);
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);

        return article;
    }

    @Transactional(readOnly = true)
    public Page<Article> getAllArticles(Pageable pageable) {
        if (pageable.isPaged()) {
            log.info("Fetching all published articles - Page: {}, Size: {}", pageable.getPageNumber(),
                    pageable.getPageSize());
        } else {
            log.info("Fetching all published articles without pagination");
        }
        return articleRepository.findByPublished(true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> getArticlesByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching articles by category ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", categoryId);
                    return new ResourceNotFoundException("Category", "id", categoryId);
                });

        return articleRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> getArticlesByTag(Long tagId, Pageable pageable) {
        log.info("Fetching articles by tag ID: {}", tagId);
        return articleRepository.findByTags_TagId(tagId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> searchArticles(String query, Pageable pageable) {
        String sanitizedQuery = HtmlSanitizer.sanitize(query);
        log.info("Searching articles with query: '{}'", sanitizedQuery);
        return articleRepository.findByTitleContainingIgnoreCase(sanitizedQuery, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> getArticlesByAuthor(Long authorId, Pageable pageable) {
        log.info("Fetching articles by author ID: {}", authorId);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Author not found with ID: {}", authorId);
                    return new ResourceNotFoundException("User", "id", authorId);
                });

        return articleRepository.findByAuthor(author, pageable);
    }

    @Transactional(readOnly = true)
    public List<Article> getLatestPublishedArticles() {
        log.info("Fetching latest published articles");
        return articleRepository.findByPublishedOrderByPublishedAtDesc(true);
    }

    private Article getArticleEntityById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("Article not found with ID: {}", articleId);
                    return new ResourceNotFoundException("Article", "id", articleId);
                });
    }

    @Transactional(readOnly = true)
    public long countAllPublishedArticles() {
        log.info("Counting total published articles");
        return articleRepository.countByPublished(true);
    }

    private void associateEmbeddedImages(Article article) {
        if (article.getContent() == null || article.getContent().isBlank()) {
            return;
        }
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(article.getContent());
        org.jsoup.select.Elements imgs = doc.select("img");
        for (org.jsoup.nodes.Element img : imgs) {
            String src = img.attr("src");
            if (src != null && src.startsWith("/uploads/media/")) {
                mediaService.getMediaByUrl(src).ifPresent(media -> {
                    mediaService.addMediaToArticle(media.getMediaId(), article.getArticleId());
                });
            }
        }
    }
}
