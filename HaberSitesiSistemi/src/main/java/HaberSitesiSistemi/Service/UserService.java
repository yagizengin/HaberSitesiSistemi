package HaberSitesiSistemi.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.PasswordChangeRequest;
import HaberSitesiSistemi.DTO.Request.UserLoginRequest;
import HaberSitesiSistemi.DTO.Request.UserRegisterRequest;
import HaberSitesiSistemi.DTO.Request.UserUpdateRequest;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User register(UserRegisterRequest request) {
        log.info("Attempting to register new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username {} already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword_hash(passwordEncoder.encode(request.getPassword()));

        User registeredUser = userRepository.save(user);
        log.info("User successfully registered with username: {}", request.getUsername());

        return registeredUser;
    }

    public User login(UserLoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with username: {}", request.getUsername());
                    return new IllegalArgumentException("Invalid username or password");
                });

        if (!user.is_active()) {
            log.warn("Login failed: User {} is deactivated", request.getUsername());
            throw new IllegalArgumentException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword_hash())) {
            log.warn("Login failed: Incorrect password for username: {}", request.getUsername());
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("User successfully logged in: {}", request.getUsername());
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.info("Fetching user details for ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found");
                });
    }

    public User updateProfile(Long userId, UserUpdateRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = getUserById(userId);

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            log.warn("Profile update failed: Username {} already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile update failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return updatedUser;
    }

    public User changePassword(Long userId, PasswordChangeRequest request) {
        log.info("Attempting password change for user ID: {}", userId);

        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword_hash())) {
            log.warn("Password change failed: Old password incorrect for user ID: {}", userId);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password change failed: New passwords do not match for user ID: {}", userId);
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword_hash(passwordEncoder.encode(request.getNewPassword()));
        User updatedUser = userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
        return updatedUser;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    public User deactivateUser(Long userId) {
        log.info("Deactivating user account for ID: {}", userId);

        User user = getUserById(userId);

        if (!user.is_active()) {
            log.warn("User ID: {} is already deactivated", userId);
            throw new IllegalArgumentException("User is already deactivated");
        }

        user.set_active(false);
        User deactivatedUser = userRepository.save(user);

        log.info("User ID: {} successfully deactivated", userId);
        return deactivatedUser;
    }

    public User reactivateUser(Long userId) {
        log.info("Reactivating user account for ID: {}", userId);

        User user = getUserById(userId);

        if (user.is_active()) {
            log.warn("User ID: {} is already active", userId);
            throw new IllegalArgumentException("User is already active");
        }

        user.set_active(true);
        User reactivatedUser = userRepository.save(user);

        log.info("User ID: {} successfully reactivated", userId);
        return reactivatedUser;
    }
}
