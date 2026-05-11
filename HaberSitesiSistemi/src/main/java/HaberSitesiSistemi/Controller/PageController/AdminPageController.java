package HaberSitesiSistemi.Controller.PageController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import HaberSitesiSistemi.DTO.Request.CategoryCreateRequest;
import HaberSitesiSistemi.DTO.Request.TagCreateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.AuditLog;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.AuditLogService;
import HaberSitesiSistemi.Service.CategoryService;
import HaberSitesiSistemi.Service.CommentService;
import HaberSitesiSistemi.Service.TagService;
import HaberSitesiSistemi.Service.UserService;
import HaberSitesiSistemi.Model.SessionLog;
import HaberSitesiSistemi.Service.SessionLogService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final CommentService commentService;
    private final AuditLogService auditLogService;
    private final SessionLogService sessionLogService;

    @GetMapping
    public String dashboard(Model model) {
        long totalUsers = userService.countAllUsers();
        long totalArticles = articleService.countAllPublishedArticles();
        long totalCategories = categoryService.countAllCategories();

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalArticles", totalArticles);
        model.addAttribute("totalCategories", totalCategories);
        return "admin/dashboard";
    }

    // ─── Users ───
    @GetMapping("/kullanicilar")
    public String usersPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<User> users = userService.getAllUsers(PageRequest.of(page, 20));
        model.addAttribute("activePage", "users");
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        
        // Editor Requests
        Page<HaberSitesiSistemi.Model.EditorRequest> pendingReqs = userService.getPendingEditorRequests(PageRequest.of(0, 50));
        model.addAttribute("pendingRequests", pendingReqs.getContent());

        return "admin/kullanicilar";
    }

    @PostMapping("/kullanici/{id}/durum")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes ra) {
        try {
            User user = userService.getUserById(id);
            if (user.isActive()) {
                userService.deactivateUser(id);
                ra.addFlashAttribute("successMsg", "Kullanıcı devre dışı bırakıldı.");
            } else {
                userService.reactivateUser(id);
                ra.addFlashAttribute("successMsg", "Kullanıcı aktifleştirildi.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/kullanicilar";
    }

    @PostMapping("/kullanici/{id}/rol-degistir")
    public String changeUserRole(@PathVariable Long id, @RequestParam String roleName, RedirectAttributes ra) {
        try {
            userService.changeUserRole(id, roleName);
            ra.addFlashAttribute("successMsg", "Kullanıcı rolü başarıyla güncellendi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Rol değiştirilirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/kullanicilar";
    }

    @PostMapping("/editor-basvuru/{id}/onayla")
    public String approveEditorRequest(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.approveEditorRequest(id);
            ra.addFlashAttribute("successMsg", "Editörlük başvurusu onaylandı. Kullanıcı artık bir Editör.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Hata: " + e.getMessage());
        }
        return "redirect:/admin/kullanicilar";
    }

    @PostMapping("/editor-basvuru/{id}/reddet")
    public String rejectEditorRequest(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.rejectEditorRequest(id);
            ra.addFlashAttribute("successMsg", "Editörlük başvurusu reddedildi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Hata: " + e.getMessage());
        }
        return "redirect:/admin/kullanicilar";
    }

    // ─── Categories ───
    @GetMapping("/kategoriler")
    public String categoriesPage(Model model) {
        model.addAttribute("activePage", "categories");
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "admin/kategoriler";
    }

    @PostMapping("/kategori-ekle")
    public String createCategory(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes ra) {
        try {
            CategoryCreateRequest req = new CategoryCreateRequest();
            req.setName(name);
            req.setDescription(description);
            categoryService.createCategory(req);
            ra.addFlashAttribute("successMsg", "Kategori oluşturuldu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/kategoriler";
    }

    @PostMapping("/kategori-sil/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("successMsg", "Kategori devre dışı bırakıldı.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/kategoriler";
    }

    // ─── Tags ───
    @GetMapping("/etiketler")
    public String tagsPage(Model model) {
        model.addAttribute("activePage", "tags");
        model.addAttribute("allTags", tagService.getAllTags());
        return "admin/etiketler";
    }

    @PostMapping("/etiket-ekle")
    public String createTag(@RequestParam String name, RedirectAttributes ra) {
        try {
            TagCreateRequest req = new TagCreateRequest();
            req.setName(name);
            tagService.createTag(req);
            ra.addFlashAttribute("successMsg", "Etiket oluşturuldu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/etiketler";
    }

    @PostMapping("/etiket-sil/{id}")
    public String deleteTag(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tagService.deleteTag(id);
            ra.addFlashAttribute("successMsg", "Etiket başarıyla silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Etiket silinirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/etiketler";
    }

    // ─── Articles ───
    @GetMapping("/makaleler")
    public String articlesPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getAllArticles(PageRequest.of(page, 20, Sort.by("publishedAt").descending()));
        model.addAttribute("activePage", "articles");
        model.addAttribute("articles", articles.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        return "admin/makaleler";
    }

    @PostMapping("/makale-sil/{id}")
    public String deleteArticle(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes ra) {
        try {
            articleService.deleteArticle(id, user.getUserId());
            ra.addFlashAttribute("successMsg", "Makale silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/makaleler";
    }

    // ─── Comments ───
    @GetMapping("/yorumlar")
    public String commentsPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Comment> comments = commentService.getAllComments(PageRequest.of(page, 20, Sort.by("createdAt").descending()));
        model.addAttribute("activePage", "comments");
        model.addAttribute("comments", comments.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comments.getTotalPages());
        return "admin/yorumlar";
    }

    @PostMapping("/yorum-onayla/{id}")
    public String approveComment(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 RedirectAttributes ra) {
        try {
            commentService.approveComment(id, user.getUserId());
            ra.addFlashAttribute("successMsg", "Yorum onaylandı.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/yorumlar";
    }

    @PostMapping("/yorum-sil/{id}")
    public String deleteComment(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes ra) {
        try {
            commentService.deleteComment(id, user.getUserId());
            ra.addFlashAttribute("successMsg", "Yorum silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/yorumlar";
    }

    // ─── Logs ───
    @GetMapping("/loglar")
    public String logsPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<AuditLog> auditLogs = auditLogService.getAuditLogs(PageRequest.of(page, 20, Sort.by("actionDate").descending()));
        Page<SessionLog> sessionLogs = sessionLogService.getAllSessionLogs(PageRequest.of(page, 20, Sort.by("loginTime").descending()));

        model.addAttribute("activePage", "logs");
        model.addAttribute("auditLogs", auditLogs.getContent());
        model.addAttribute("sessionLogs", sessionLogs.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(auditLogs.getTotalPages(), sessionLogs.getTotalPages()));
        
        return "admin/loglar";
    }
}
