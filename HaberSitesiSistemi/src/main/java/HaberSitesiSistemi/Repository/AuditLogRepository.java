package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
