package HaberSitesiSistemi.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.AuditLog;
import HaberSitesiSistemi.Model.User;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUser(User user, Pageable pageable);

    Page<AuditLog> findByTableName(String tableName, Pageable pageable);
}
