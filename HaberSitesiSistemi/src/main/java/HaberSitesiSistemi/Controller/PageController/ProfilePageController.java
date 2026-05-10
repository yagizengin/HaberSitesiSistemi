package HaberSitesiSistemi.Controller.PageController;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import HaberSitesiSistemi.DTO.Request.PasswordChangeRequest;
import HaberSitesiSistemi.DTO.Request.UserUpdateRequest;
import HaberSitesiSistemi.Model.SavedArticle;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.CategoryService;
import HaberSitesiSistemi.Service.SavedArticleService;
import HaberSitesiSistemi.Service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profil")
@RequiredArgsConstructor
public class ProfilePageController {

    private final UserService userService;
    private final SavedArticleService savedArticleService;
    private final CategoryService categoryService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam(defaultValue = "personal") String tab,
                              Model model) {
        User user = userService.getUserById(userDetails.getUserId());
        Page<SavedArticle> savedArticles = savedArticleService.getUserSavedArticles(
                userDetails.getUserId(), PageRequest.of(0, 50));

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("user", user);
        model.addAttribute("savedArticles", savedArticles.getContent());
        model.addAttribute("activeTab", tab);

        userService.getEditorRequestForUser(user.getUserId()).ifPresent(req -> {
            model.addAttribute("editorRequest", req);
        });

        return "profil";
    }

    @PostMapping("/guncelle")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam String username,
                                @RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            UserUpdateRequest request = new UserUpdateRequest();
            request.setUsername(username);
            request.setEmail(email);
            userService.updateProfile(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMsg", "Profil güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/profil";
    }

    @PostMapping("/sifre-degistir")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            PasswordChangeRequest request = new PasswordChangeRequest();
            request.setOldPassword(oldPassword);
            request.setNewPassword(newPassword);
            request.setConfirmPassword(confirmPassword);
            userService.changePassword(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMsg", "Şifre değiştirildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/profil?tab=password";
    }

    @PostMapping("/sifremi-unuttum")
    public String forgotPasswordProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(userDetails.getUserId());
            userService.forgotPassword(user.getEmail());
            redirectAttributes.addFlashAttribute("successMsg", "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Mail gönderilirken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/profil?tab=password";
    }

    @PostMapping("/editor-basvurusu")
    public String requestEditorRole(@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes ra) {
        try {
            userService.submitEditorRequest(userDetails.getUserId());
            ra.addFlashAttribute("successMsg", "Editörlük başvurunuz başarıyla alındı.");
        } catch (HaberSitesiSistemi.Exception.ConflictException e) {
            if (e.getMessage().contains("resubmitted")) {
                ra.addFlashAttribute("successMsg", "Önceki başvurunuz reddedilmişti. Yeniden başvuru yaptınız.");
            } else {
                ra.addFlashAttribute("errorMsg", e.getMessage());
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Başvuru sırasında bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/profil?tab=personal";
    }
}
