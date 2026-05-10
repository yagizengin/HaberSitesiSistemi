package HaberSitesiSistemi.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import HaberSitesiSistemi.DTO.Request.ForgotPasswordRequest;
import HaberSitesiSistemi.DTO.Request.ResetPasswordRequest;

import HaberSitesiSistemi.DTO.Request.PasswordChangeRequest;
import HaberSitesiSistemi.DTO.Request.UserLoginRequest;
import HaberSitesiSistemi.DTO.Request.UserRegisterRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.LoginResponseDTO;
import HaberSitesiSistemi.DTO.Response.UserResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Security.JwtTokenProvider;
import HaberSitesiSistemi.Service.SessionLogService;
import HaberSitesiSistemi.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SessionLogService sessionLogService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = userService.register(request);
        UserResponseDTO data = EntityDtoMapper.toUserResponseDTO(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("Kayıt başarılı. Lütfen giriş yapabilmek için mail adresinize gönderilen aktivasyon linkine tıklayarak hesabınızı onaylayın.")
                        .data(data)
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);

            sessionLogService.logLogin(ipAddress, userDetails.getUserId(), true);

            LoginResponseDTO data = EntityDtoMapper.toLoginResponseDTO(
                    token, jwtTokenProvider.getExpirationMs(), 
                    userService.getUserById(userDetails.getUserId()));

            return ResponseEntity.ok(ApiResponse.<LoginResponseDTO>builder()
                    .success(true)
                    .message("Login successful")
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build());

        } catch (Exception e) {
            sessionLogService.logLogin(ipAddress, null, false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message("Invalid username or password")
                            .timestamp(System.currentTimeMillis())
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam Long sessionId) {
        sessionLogService.logLogout(sessionId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out successfully")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {

        userService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Email verified successfully. You can now login.")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("If an account exists with that email, a password reset link has been sent.")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password has been reset successfully.")
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
