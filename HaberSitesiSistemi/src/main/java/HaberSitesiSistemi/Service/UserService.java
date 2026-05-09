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
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ConflictException;
import HaberSitesiSistemi.Exception.UnauthorizedException;
import HaberSitesiSistemi.Exception.ForbiddenException;
import HaberSitesiSistemi.Exception.BadRequestException;
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
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User registeredUser = userRepository.save(user);
        log.info("User successfully registered with username: {}", request.getUsername());

        return registeredUser;
    }

    public User login(UserLoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with username: {}", request.getUsername());
                    return new UnauthorizedException("Invalid username or password");
                });

        if (!user.isActive()) {
            log.warn("Login failed: User {} is deactivated", request.getUsername());
            throw new ForbiddenException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: Incorrect password for username: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
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
                    return new ResourceNotFoundException("User", "id", userId);
                });
    }

    public User updateProfile(Long userId, UserUpdateRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = getUserById(userId);

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            log.warn("Profile update failed: Username {} already exists", request.getUsername());
            throw new ConflictException("Username already exists");
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile update failed: Email {} already exists", request.getEmail());
            throw new ConflictException("Email already exists");
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

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            log.warn("Password change failed: Old password incorrect for user ID: {}", userId);
            throw new BadRequestException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password change failed: New passwords do not match for user ID: {}", userId);
            throw new BadRequestException("New password and confirm password do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        User updatedUser = userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
        return updatedUser;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        if (pageable.isPaged()) {
            log.info("Fetching all users with pagination - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        } else {
            log.info("Fetching all users without pagination");
        }
        return userRepository.findAll(pageable);
    }

    public User deactivateUser(Long userId) {
        log.info("Deactivating user account for ID: {}", userId);

        User user = getUserById(userId);

        if (!user.isActive()) {
            log.warn("User ID: {} is already deactivated", userId);
            throw new ConflictException("User is already deactivated");
        }

        user.setActive(false);
        User deactivatedUser = userRepository.save(user);

        log.info("User ID: {} successfully deactivated", userId);
        return deactivatedUser;
    }

    public User reactivateUser(Long userId) {
        log.info("Reactivating user account for ID: {}", userId);

        User user = getUserById(userId);

        if (user.isActive()) {
            log.warn("User ID: {} is already active", userId);
            throw new ConflictException("User is already active");
        }

        user.setActive(true);
        User reactivatedUser = userRepository.save(user);

        log.info("User ID: {} successfully reactivated", userId);
        return reactivatedUser;
    }

    @Transactional(readOnly = true)
    public long countAllUsers() {
        log.info("Counting total users");
        return userRepository.count();
    }
}
