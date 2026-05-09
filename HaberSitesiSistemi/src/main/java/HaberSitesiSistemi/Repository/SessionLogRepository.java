package HaberSitesiSistemi.Repository;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.SessionLog;
import HaberSitesiSistemi.Model.User;

public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {

    Page<SessionLog> findByUser(User user, Pageable pageable);

    long countByIpAddressAndSuccessAndLoginTimeAfter(String ipAddress, boolean success, Timestamp loginTime);

    Page<SessionLog> findByIpAddressOrderByLoginTimeDesc(String ipAddress, Pageable pageable);
}
