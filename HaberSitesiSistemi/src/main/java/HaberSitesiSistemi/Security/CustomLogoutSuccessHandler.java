package HaberSitesiSistemi.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import HaberSitesiSistemi.Repository.SessionLogRepository;
import HaberSitesiSistemi.Service.SessionLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SessionLogService sessionLogService;
    private final SessionLogRepository sessionLogRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
            String ipAddress = request.getRemoteAddr();
            
            // Find the most recent active session for this user and IP to log out
            // Since we don't store sessionId in HTTP session natively in this setup, 
            // we look up the latest open session.
            try {
                sessionLogRepository.findFirstByUser_UserIdAndIpAddressAndSuccessTrueAndLogoutTimeIsNullOrderByLoginTimeDesc(userId, ipAddress)
                    .ifPresent(sessionLog -> {
                        sessionLogService.logLogout(sessionLog.getSessionId());
                        log.debug("Logged out session ID {} for User ID {}", sessionLog.getSessionId(), userId);
                    });
            } catch (Exception e) {
                log.error("Failed to log logout session", e);
            }
        }

        // Redirect back to login page with logout message
        response.sendRedirect("/giris?logout=true");
    }
}
