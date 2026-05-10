package HaberSitesiSistemi.Security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final SessionLogService sessionLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        String ipAddress = request.getRemoteAddr();
        
        // In a failed login, we don't have a confirmed user, so userId is null
        log.debug("Failed login attempt from IP {}", ipAddress);
        
        try {
            sessionLogService.logLogin(ipAddress, null, false);
        } catch (Exception e) {
            log.error("Failed to log unsuccessful session", e);
        }

        // Redirect back to login page with error
        response.sendRedirect("/giris?error=true");
    }
}
