package HaberSitesiSistemi.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import HaberSitesiSistemi.Service.SessionLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionLogService sessionLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        String ipAddress = request.getRemoteAddr();
        
        Long userId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }

        log.debug("Successful login from IP {} for User ID {}", ipAddress, userId);
        
        try {
            sessionLogService.logLogin(ipAddress, userId, true);
        } catch (Exception e) {
            log.error("Failed to log successful session", e);
        }

        // Redirect to home page or dashboard after successful login
        response.sendRedirect("/");
    }
}
