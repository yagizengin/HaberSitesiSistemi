package HaberSitesiSistemi.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Model.User;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {

    Optional<SavedArticle> findByUserAndArticle(User user, Article article);

    Page<SavedArticle> findByUser(User user, Pageable pageable);

    boolean existsByUserAndArticle(User user, Article article);
}
