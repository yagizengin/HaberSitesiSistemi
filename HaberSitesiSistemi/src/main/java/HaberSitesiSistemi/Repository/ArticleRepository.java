package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    
}
