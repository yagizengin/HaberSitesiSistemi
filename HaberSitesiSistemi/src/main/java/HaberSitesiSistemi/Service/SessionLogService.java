package HaberSitesiSistemi.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.Model.SessionLog;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.SessionLogRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SessionLogService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final SessionLogRepository sessionLogRepository;
    private final UserRepository userRepository;

    public SessionLog logLogin(String ipAddress, Long userId, boolean success) {
        log.info("Logging login attempt from IP: {} for user: {} success: {}", ipAddress, userId, success);

        SessionLog sessionLog = new SessionLog();
        sessionLog.setIp_address(ipAddress);
        sessionLog.set_success(success);

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            sessionLog.setUser(user);
        }

        return sessionLogRepository.save(sessionLog);
    }

    public SessionLog logLogout(Long sessionId) {
        log.info("Logging logout for session ID: {}", sessionId);

        SessionLog sessionLog = sessionLogRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        sessionLog.setLogout_time(Timestamp.valueOf(LocalDateTime.now()));
        return sessionLogRepository.save(sessionLog);
    }

    @Transactional(readOnly = true)
    public Page<SessionLog> getSessionHistory(Long userId, Pageable pageable) {
        log.info("Fetching session history for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return sessionLogRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public long detectBruteForce(String ipAddress) {
        Timestamp threshold = Timestamp.valueOf(
                LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES));
        long count = sessionLogRepository
                .countByIpAddressAndIsSuccessAndLoginTimeAfter(ipAddress, false, threshold);
        log.info("Brute force check for IP {}: {} failed attempts in last {} min",
                ipAddress, count, BLOCK_DURATION_MINUTES);
        return count;
    }

    @Transactional(readOnly = true)
    public boolean isIPBlocked(String ipAddress) {
        return detectBruteForce(ipAddress) >= MAX_FAILED_ATTEMPTS;
    }
}
