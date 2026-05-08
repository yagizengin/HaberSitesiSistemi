package HaberSitesiSistemi.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.Model.AuditLog;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.AuditLogRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLog logAction(String actionType, String tableName, Long recordId, Long userId) {
        log.info("Logging audit action: {} on {}.{} by user {}", actionType, tableName, recordId, userId);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction_type(actionType);
        auditLog.setTable_name(tableName);
        auditLog.setRecord_id(recordId);

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            auditLog.setUser(user);
        }

        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        log.info("Fetching all audit logs");
        return auditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        log.info("Fetching audit logs for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return auditLogRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByTable(String tableName, Pageable pageable) {
        log.info("Fetching audit logs for table: {}", tableName);
        return auditLogRepository.findByTableName(tableName, pageable);
    }
}
