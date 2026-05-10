package HaberSitesiSistemi.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendActivationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Hesap Aktivasyonu - Haber Sitesi");
            // Localhost for development, can be configured in properties for production
            String activationUrl = "http://localhost:8080/verify-email?token=" + token;
            message.setText("Merhaba,\n\nHesabınızı aktifleştirmek için lütfen aşağıdaki linke tıklayın:\n" 
                            + activationUrl + "\n\nBu link 24 saat geçerlidir.");
            
            mailSender.send(message);
            log.info("Activation email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}", to, e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Şifre Sıfırlama Talebi - Haber Sitesi");
            // Localhost for development, can be configured in properties for production
            String resetUrl = "http://localhost:8080/sifre-sifirla?token=" + token; 
            message.setText("Merhaba,\n\nŞifrenizi sıfırlamak için lütfen aşağıdaki linke tıklayın:\n" 
                            + resetUrl + "\n\nBu link 15 dakika geçerlidir. Eğer bu talebi siz yapmadıysanız lütfen bu maili dikkate almayın.");
            
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", to, e);
        }
    }
}
