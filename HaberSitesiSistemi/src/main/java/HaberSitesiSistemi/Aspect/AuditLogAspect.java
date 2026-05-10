package HaberSitesiSistemi.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Model.Tag;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    // Intercept methods returning an entity or void, starting with specific prefixes
    @AfterReturning(
            pointcut = "execution(* HaberSitesiSistemi.Service.*Service.create*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.update*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.delete*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.approve*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.publish*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.register*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.changePassword*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.deactivate*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.saveArticle*(..)) || " +
                       "execution(* HaberSitesiSistemi.Service.*Service.unsaveArticle*(..))",
            returning = "result"
    )
    public void logAfterModifyingAction(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String actionType = determineActionType(methodName);
        String tableName = determineTableName(methodName, result);
        Long recordId = determineRecordId(result, joinPoint);
        Long userId = getCurrentUserId();

        if (actionType != null && tableName != null) {
            log.debug("AOP Audit Log: {} on {} ID: {} by User: {}", actionType, tableName, recordId, userId);
            try {
                auditLogService.logAction(actionType, tableName, recordId, userId);
            } catch (Exception e) {
                log.error("Failed to save audit log for action: {}", actionType, e);
            }
        }
    }

    private String determineActionType(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("register") || methodName.startsWith("saveArticle")) {
            return "CREATE";
        } else if (methodName.startsWith("update") || methodName.startsWith("changePassword")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("unsaveArticle")) {
            return "DELETE";
        } else if (methodName.startsWith("approve")) {
            return "APPROVE";
        } else if (methodName.startsWith("publish")) {
            return "PUBLISH";
        } else if (methodName.startsWith("deactivate")) {
            return "DEACTIVATE";
        }
        return "UNKNOWN";
    }

    private String determineTableName(String methodName, Object result) {
        if (result instanceof Article) return "articles";
        if (result instanceof Category) return "categories";
        if (result instanceof Tag) return "tags";
        if (result instanceof Comment) return "comments";
        if (result instanceof User) return "users";
        if (result instanceof SavedArticle) return "saved_articles";

        // Fallback for void methods (e.g., deleteArticle, unsaveArticle)
        String lowerMethod = methodName.toLowerCase();
        if (lowerMethod.contains("article")) return "articles";
        if (lowerMethod.contains("category")) return "categories";
        if (lowerMethod.contains("tag")) return "tags";
        if (lowerMethod.contains("comment")) return "comments";
        if (lowerMethod.contains("user") || lowerMethod.contains("password")) return "users";

        return "unknown";
    }

    private Long determineRecordId(Object result, JoinPoint joinPoint) {
        // Try to get ID from returned entity
        if (result instanceof Article) return ((Article) result).getArticleId();
        if (result instanceof Category) return ((Category) result).getCategoryId();
        if (result instanceof Tag) return ((Tag) result).getTagId();
        if (result instanceof Comment) return ((Comment) result).getCommentId();
        if (result instanceof User) return ((User) result).getUserId();
        if (result instanceof SavedArticle) return ((SavedArticle) result).getSaveId();

        // If method is void, try to find an ID parameter (usually the first or second arg is the ID)
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return null;
    }
}
