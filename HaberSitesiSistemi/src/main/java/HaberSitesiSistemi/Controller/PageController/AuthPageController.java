package HaberSitesiSistemi.Controller.PageController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import HaberSitesiSistemi.DTO.Request.UserRegisterRequest;
import HaberSitesiSistemi.Service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final UserService userService;

    @GetMapping("/giris")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Kullanıcı adı veya şifre hatalı.");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Başarıyla çıkış yapıldı.");
        }
        return "giris";
    }

    @PostMapping("/kayit")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        try {
            UserRegisterRequest request = new UserRegisterRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);

            userService.register(request);

            redirectAttributes.addFlashAttribute("successMsg", "Hesap oluşturuldu! Giriş yapabilirsiniz.");
            return "redirect:/giris";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "register");
            return "redirect:/giris";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        try {
            userService.verifyEmail(token);
            redirectAttributes.addFlashAttribute("successMsg", "E-posta adresiniz doğrulandı. Artık giriş yapabilirsiniz.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Geçersiz veya süresi dolmuş bağlantı.");
        }
        return "redirect:/giris";
    }

    @PostMapping("/sifremi-unuttum")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.forgotPassword(email);
            redirectAttributes.addFlashAttribute("successMsg", "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.");
            redirectAttributes.addFlashAttribute("activeTab", "forgot");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Bu e-posta adresi sistemde bulunamadı veya bir hata oluştu.");
            redirectAttributes.addFlashAttribute("activeTab", "forgot");
        }
        return "redirect:/giris";
    }

    @GetMapping("/sifre-sifirla")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "sifre-sifirla";
    }

    @PostMapping("/sifre-sifirla")
    public String processResetPassword(@RequestParam String token, 
                                       @RequestParam String newPassword, 
                                       RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("successMsg", "Şifreniz başarıyla sıfırlandı. Yeni şifrenizle giriş yapabilirsiniz.");
            return "redirect:/giris";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Sıfırlama işlemi başarısız. Bağlantı geçersiz veya süresi dolmuş olabilir.");
            return "redirect:/sifre-sifirla?token=" + token;
        }
    }
}
