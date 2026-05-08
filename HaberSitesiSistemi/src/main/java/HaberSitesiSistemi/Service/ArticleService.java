package HaberSitesiSistemi.Service;

import java.sql.Timestamp;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
                    return new IllegalArgumentException("Author not found");
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Article creation failed: Category not found with ID: {}", request.getCategoryId());
                    return new IllegalArgumentException("Category not found");
                });

        Article article = new Article();
        article.setTitle(HtmlSanitizer.sanitize(request.getTitle()));
        article.setContent(HtmlSanitizer.sanitizeHtml(request.getContent()));
        article.setAuthor(author);
        article.setCategory(category);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tags::add);
            }
            article.setTags(tags);
        }

        Article savedArticle = articleRepository.save(article);
        log.info("Article created successfully with ID: {}", savedArticle.getArticle_id());
        return savedArticle;
    }

    public Article updateArticle(Long articleId, ArticleUpdateRequest request, Long userId) {
        log.info("Updating article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        if (!article.getAuthor().getUser_id().equals(userId)) {
            log.warn("Update denied: User {} is not the author of article {}", userId, articleId);
            throw new IllegalArgumentException("You are not authorized to update this article");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Article update failed: Category not found with ID: {}", request.getCategoryId());
                    return new IllegalArgumentException("Category not found");
                });

        article.setTitle(HtmlSanitizer.sanitize(request.getTitle()));
        article.setContent(HtmlSanitizer.sanitizeHtml(request.getContent()));
        article.setCategory(category);

        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tags::add);
            }
            article.setTags(tags);
        }

        Article updatedArticle = articleRepository.save(article);
        log.info("Article {} updated successfully", articleId);
        return updatedArticle;
    }

    public void deleteArticle(Long articleId, Long userId) {
        log.info("Deleting article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        if (!article.getAuthor().getUser_id().equals(userId)) {
            log.warn("Delete denied: User {} is not the author of article {}", userId, articleId);
            throw new IllegalArgumentException("You are not authorized to delete this article");
        }
        mediaService.deleteMediaByArticleId(articleId);
        articleRepository.delete(article);
        log.info("Article {} deleted successfully", articleId);
    }

    public Article publishArticle(Long articleId, Long userId) {
        log.info("Publishing article ID: {} by user ID: {}", articleId, userId);

        Article article = getArticleEntityById(articleId);

        if (article.is_published()) {
            log.warn("Article {} is already published", articleId);
            throw new IllegalArgumentException("Article is already published");
        }

        article.set_published(true);
        article.setPublished_at(Timestamp.valueOf(LocalDateTime.now()));

        Article publishedArticle = articleRepository.save(article);
        log.info("Article {} published successfully", articleId);
        return publishedArticle;
    }

    public Article getArticleById(Long articleId) {
        log.info("Fetching article with ID: {}", articleId);

        Article article = getArticleEntityById(articleId);
        article.setView_count(article.getView_count() + 1);
        articleRepository.save(article);

        return article;
    }

    @Transactional(readOnly = true)
    public Page<Article> getAllArticles(Pageable pageable) {
        log.info("Fetching all published articles - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return articleRepository.findByIsPublished(true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> getArticlesByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching articles by category ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", categoryId);
                    return new IllegalArgumentException("Category not found");
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
                    return new IllegalArgumentException("Author not found");
                });

        return articleRepository.findByAuthor(author, pageable);
    }

    @Transactional(readOnly = true)
    public List<Article> getLatestPublishedArticles() {
        log.info("Fetching latest published articles");
        return articleRepository.findByIsPublishedOrderByPublishedAtDesc(true);
    }

    private Article getArticleEntityById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("Article not found with ID: {}", articleId);
                    return new IllegalArgumentException("Article not found");
                });
    }
}
