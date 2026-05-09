package HaberSitesiSistemi.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticle(Article article, Pageable pageable);

    Page<Comment> findByArticleAndApproved(Article article, boolean approved, Pageable pageable);

    Page<Comment> findByUser(User user, Pageable pageable);

    Page<Comment> findByApproved(boolean approved, Pageable pageable);
}
