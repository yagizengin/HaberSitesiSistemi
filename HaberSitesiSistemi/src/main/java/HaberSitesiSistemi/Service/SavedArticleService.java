package HaberSitesiSistemi.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.ArticleRepository;
import HaberSitesiSistemi.Repository.SavedArticleRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SavedArticleService {

    private final SavedArticleRepository savedArticleRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public SavedArticle saveArticle(Long userId, Long articleId) {
        log.info("Saving article {} for user {}", articleId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if (savedArticleRepository.existsByUserAndArticle(user, article)) {
            throw new ConflictException("Article is already saved");
        }

        SavedArticle saved = new SavedArticle();
        saved.setUser(user);
        saved.setArticle(article);
        SavedArticle result = savedArticleRepository.save(saved);
        log.info("Article {} saved for user {} with ID: {}", articleId, userId, result.getSaveId());
        return result;
    }

    public void unsaveArticle(Long userId, Long articleId) {
        log.info("Unsaving article {} for user {}", articleId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        SavedArticle saved = savedArticleRepository.findByUserAndArticle(user, article)
                .orElseThrow(() -> new ResourceNotFoundException("SavedArticle", "user_id, article_id", userId + ", " + articleId));
        savedArticleRepository.delete(saved);
        log.info("Article {} unsaved for user {}", articleId, userId);
    }

    @Transactional(readOnly = true)
    public Page<SavedArticle> getUserSavedArticles(Long userId, Pageable pageable) {
        log.info("Fetching saved articles for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return savedArticleRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isArticleSaved(Long userId, Long articleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        return savedArticleRepository.existsByUserAndArticle(user, article);
    }
}
