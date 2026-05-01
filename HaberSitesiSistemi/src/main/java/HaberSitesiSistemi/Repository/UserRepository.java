package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import HaberSitesiSistemi.Model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
