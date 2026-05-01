package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.SavedArticle;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {

}
