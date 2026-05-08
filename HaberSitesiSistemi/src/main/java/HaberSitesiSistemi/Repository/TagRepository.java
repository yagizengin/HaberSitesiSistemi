package HaberSitesiSistemi.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
