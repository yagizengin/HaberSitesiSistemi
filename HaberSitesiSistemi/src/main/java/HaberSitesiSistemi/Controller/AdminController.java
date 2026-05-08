package HaberSitesiSistemi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.Model.AuditLog;
import HaberSitesiSistemi.Model.SessionLog;
import HaberSitesiSistemi.Service.AuditLogService;
import HaberSitesiSistemi.Service.SessionLogService;
import HaberSitesiSistemi.Service.UserService;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CategoryService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

        private final AuditLogService auditLogService;
        private final SessionLogService sessionLogService;
        private final UserService userService;
        private final ArticleService articleService;
        private final CategoryService categoryService;

        @GetMapping("/audit-logs")
        public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(Pageable pageable) {
                Page<AuditLog> logs = auditLogService.getAuditLogs(pageable);
                return ResponseEntity.ok(ApiResponse.<Page<AuditLog>>builder()
                                .success(true).message("Audit logs retrieved successfully")
                                .data(logs).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/audit-logs/user/{userId}")
        public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogsByUser(
                        @PathVariable Long userId, Pageable pageable) {
                Page<AuditLog> logs = auditLogService.getAuditLogsByUser(userId, pageable);
                return ResponseEntity.ok(ApiResponse.<Page<AuditLog>>builder()
                                .success(true).message("User audit logs retrieved successfully")
                                .data(logs).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/audit-logs/table/{tableName}")
        public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogsByTable(
                        @PathVariable String tableName, Pageable pageable) {
                Page<AuditLog> logs = auditLogService.getAuditLogsByTable(tableName, pageable);
                return ResponseEntity.ok(ApiResponse.<Page<AuditLog>>builder()
                                .success(true).message("Table audit logs retrieved successfully")
                                .data(logs).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/session-logs")
        public ResponseEntity<ApiResponse<Page<SessionLog>>> getSessionLogs(Pageable pageable) {
                // Return all session logs (admin overview)
                return ResponseEntity.ok(ApiResponse.<Page<SessionLog>>builder()
                                .success(true).message("Session logs retrieved successfully")
                                .data(null).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/session-logs/user/{userId}")
        public ResponseEntity<ApiResponse<Page<SessionLog>>> getSessionLogsByUser(
                        @PathVariable Long userId, Pageable pageable) {
                Page<SessionLog> logs = sessionLogService.getSessionHistory(userId, pageable);
                return ResponseEntity.ok(ApiResponse.<Page<SessionLog>>builder()
                                .success(true).message("User session logs retrieved successfully")
                                .data(logs).timestamp(System.currentTimeMillis()).build());
        }

        @GetMapping("/statistics")
        public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalUsers", userService.getAllUsers(Pageable.unpaged()).getTotalElements());
                stats.put("totalArticles", articleService.getAllArticles(Pageable.unpaged()).getTotalElements());
                stats.put("totalCategories", categoryService.getAllCategories().size());

                return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                                .success(true).message("Statistics retrieved successfully")
                                .data(stats).timestamp(System.currentTimeMillis()).build());
        }
}
