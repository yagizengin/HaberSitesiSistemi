package HaberSitesiSistemi.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.PasswordChangeRequest;
import HaberSitesiSistemi.DTO.Request.UserLoginRequest;
import HaberSitesiSistemi.DTO.Request.UserRegisterRequest;
import HaberSitesiSistemi.DTO.Request.UserUpdateRequest;
import HaberSitesiSistemi.Model.PasswordResetToken;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Model.VerificationToken;
import HaberSitesiSistemi.Repository.PasswordResetTokenRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import HaberSitesiSistemi.Repository.VerificationTokenRepository;
import HaberSitesiSistemi.Repository.RoleRepository;
import HaberSitesiSistemi.Repository.EditorRequestRepository;
import HaberSitesiSistemi.Model.EditorRequest;
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
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final EditorRequestRepository editorRequestRepository;

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
        user.setActive(false); // Make sure it's false before verification

        HaberSitesiSistemi.Model.Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        user.getRoles().add(userRole);

        User registeredUser = userRepository.save(user);

        // Generate verification token and send email
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setUser(registeredUser);
        vToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(vToken);

        emailService.sendActivationEmail(registeredUser.getEmail(), token);

        log.info("User successfully registered and activation email sent to: {}", request.getEmail());

        return registeredUser;
    }

    public void verifyEmail(String token) {
        VerificationToken vToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        User user = vToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        // Optional: Delete the token after successful verification
        verificationTokenRepository.delete(vToken);
        log.info("User email verified successfully for user: {}", user.getUsername());
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
        log.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token so it can't be reused
        passwordResetTokenRepository.delete(resetToken);
        log.info("Password successfully reset for user: {}", user.getUsername());
    }

    public User login(UserLoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with username: {}", request.getUsername());
                    return new UnauthorizedException("Invalid username or password");
                });

        if (!user.isActive()) {
            log.warn("Login failed: User {} is deactivated or not verified", request.getUsername());
            throw new ForbiddenException("User account is deactivated or email is not verified");
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

        // Email güncelleme şimdilik iptal edildi
        /*
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile update failed: Email {} already exists", request.getEmail());
            throw new ConflictException("Email already exists");
        }

        user.setEmail(request.getEmail());
        */

        user.setUsername(request.getUsername());

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

    public User changeUserRole(Long userId, String roleName) {
        log.info("Changing role for user ID: {} to {}", userId, roleName);
        User user = getUserById(userId);
        
        HaberSitesiSistemi.Model.Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                
        user.getRoles().clear();
        user.getRoles().add(role);

        // Eğer kullanıcı Editör rolünden başka bir role düşürülüyorsa (örneğin geri USER yapılıyorsa)
        // eski onaylanmış başvurusunu silmeliyiz ki gelecekte tekrar başvuru yapabilsin.
        if (!roleName.equals("ROLE_EDITOR")) {
            editorRequestRepository.findByUser(user).ifPresent(req -> {
                if ("APPROVED".equals(req.getStatus())) {
                    editorRequestRepository.delete(req);
                }
            });
        }

        return userRepository.save(user);
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

    public void submitEditorRequest(Long userId) {
        User user = getUserById(userId);
        
        // Check if user is already an editor or admin
        boolean hasPrivilege = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_EDITOR") || r.getName().equals("ROLE_ADMIN"));
        if (hasPrivilege) {
            throw new ConflictException("User is already an Editor or Admin");
        }

        // Check if there is an existing pending request
        editorRequestRepository.findByUser(user).ifPresent(req -> {
            if (req.getStatus().equals("PENDING")) {
                throw new ConflictException("You already have a pending editor request");
            } else if (req.getStatus().equals("REJECTED")) {
                // If previously rejected, allow reapplying by resetting status
                req.setStatus("PENDING");
                req.setCreatedAt(LocalDateTime.now());
                editorRequestRepository.save(req);
                throw new ConflictException("Your previous request was rejected. We have resubmitted your application."); // Using exception for flow control in simple case, better to just return
            }
        });

        // If no existing request, create new
        if (editorRequestRepository.findByUser(user).isEmpty()) {
            EditorRequest request = new EditorRequest();
            request.setUser(user);
            editorRequestRepository.save(request);
        }
    }

    @Transactional(readOnly = true)
    public Optional<EditorRequest> getEditorRequestForUser(Long userId) {
        User user = getUserById(userId);
        return editorRequestRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<EditorRequest> getPendingEditorRequests(Pageable pageable) {
        return editorRequestRepository.findByStatus("PENDING", pageable);
    }

    public void approveEditorRequest(Long requestId) {
        EditorRequest request = editorRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EditorRequest", "id", requestId));
        
        request.setStatus("APPROVED");
        editorRequestRepository.save(request);

        // Change role to Editor
        changeUserRole(request.getUser().getUserId(), "ROLE_EDITOR");
    }

    public void rejectEditorRequest(Long requestId) {
        EditorRequest request = editorRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EditorRequest", "id", requestId));
        
        request.setStatus("REJECTED");
        editorRequestRepository.save(request);
    }
}
