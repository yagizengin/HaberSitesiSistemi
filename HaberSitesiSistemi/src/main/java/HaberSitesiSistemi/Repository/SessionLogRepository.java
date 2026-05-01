package HaberSitesiSistemi.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import HaberSitesiSistemi.Model.SessionLog;

public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {

}
