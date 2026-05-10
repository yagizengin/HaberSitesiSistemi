package HaberSitesiSistemi.Security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.UserRepository;
import HaberSitesiSistemi.Service.SessionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final SessionLogService sessionLogService;
    private final UserRepository userRepository;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String ipAddress = getClientIP();
        Object principal = event.getAuthentication().getPrincipal();
        Long userId = null;

        if (principal instanceof CustomUserDetails) {
            userId = ((CustomUserDetails) principal).getUserId();
        } else if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        if (userId != null) {
            sessionLogService.logLogin(ipAddress, userId, true);
            log.info("Login SUCCESS logged for user ID {}", userId);
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String ipAddress = getClientIP();
        Object principal = event.getAuthentication().getPrincipal();
        Long userId = null;

        if (principal instanceof String) {
            String username = (String) principal;
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        sessionLogService.logLogin(ipAddress, userId, false);
        log.warn("Login FAILURE logged for IP {}. Username attempted: {}", ipAddress, principal);
    }

    private String getClientIP() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader == null) {
                return request.getRemoteAddr();
            }
            return xfHeader.split(",")[0];
        }
        return "Unknown";
    }
}
