package HaberSitesiSistemi.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findByArticle(Article article);

    java.util.Optional<Media> findByFileUrl(String fileUrl);
}
