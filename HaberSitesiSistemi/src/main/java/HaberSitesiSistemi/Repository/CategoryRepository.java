package HaberSitesiSistemi.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActive(boolean active);

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    long countByActive(boolean active);
}
