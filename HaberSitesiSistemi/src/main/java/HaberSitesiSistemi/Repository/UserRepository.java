package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import HaberSitesiSistemi.Model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
