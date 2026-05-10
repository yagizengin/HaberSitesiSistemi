package HaberSitesiSistemi.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.EditorRequest;
import HaberSitesiSistemi.Model.User;

public interface EditorRequestRepository extends JpaRepository<EditorRequest, Long> {
    Optional<EditorRequest> findByUser(User user);
    Page<EditorRequest> findByStatus(String status, Pageable pageable);
}
