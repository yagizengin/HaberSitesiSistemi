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
}
