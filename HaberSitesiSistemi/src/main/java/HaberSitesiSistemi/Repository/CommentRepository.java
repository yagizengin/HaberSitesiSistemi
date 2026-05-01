package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}
