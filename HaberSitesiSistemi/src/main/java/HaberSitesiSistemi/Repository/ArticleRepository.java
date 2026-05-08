package HaberSitesiSistemi.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Model.User;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByIsPublished(boolean isPublished, Pageable pageable);

    Page<Article> findByCategory(Category category, Pageable pageable);

    Page<Article> findByAuthor(User author, Pageable pageable);

    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Article> findByIsPublishedOrderByPublishedAtDesc(boolean isPublished);

    Page<Article> findByTags_TagId(Long tagId, Pageable pageable);
}
