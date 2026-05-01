package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
