package HaberSitesiSistemi.Security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import HaberSitesiSistemi.Service.SessionLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IpBlockingFilter extends OncePerRequestFilter {

    private final SessionLogService sessionLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if the request is a login attempt
        String uri = request.getRequestURI();
        if ("POST".equalsIgnoreCase(request.getMethod()) && (uri.equals("/giris") || uri.equals("/api/auth/login"))) {
            String ipAddress = request.getRemoteAddr();
            
            if (sessionLogService.isIPBlocked(ipAddress)) {
                if (uri.equals("/api/auth/login")) {
                    response.setStatus(429); // TOO_MANY_REQUESTS
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Too many failed login attempts. Please try again later.\",\"timestamp\":" + System.currentTimeMillis() + "}");
                    return;
                } else {
                    response.sendRedirect("/giris?error=blocked");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
