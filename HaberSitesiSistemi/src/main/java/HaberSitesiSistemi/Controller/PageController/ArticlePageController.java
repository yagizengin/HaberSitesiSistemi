package HaberSitesiSistemi.Controller.PageController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import HaberSitesiSistemi.DTO.Request.CommentCreateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CategoryService;
import HaberSitesiSistemi.Service.CommentService;
import HaberSitesiSistemi.Service.SavedArticleService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ArticlePageController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final SavedArticleService savedArticleService;
    private final CategoryService categoryService;

    @GetMapping("/haber/{id}")
    public String articleDetail(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails user,
                                Model model) {
        Article article = articleService.getPublishedArticleById(id);
        Page<Comment> comments = commentService.getApprovedComments(id, PageRequest.of(0, 50));

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("article", article);
        model.addAttribute("comments", comments.getContent());

        if (user != null) {
            boolean isSaved = savedArticleService.isArticleSaved(user.getUserId(), id);
            model.addAttribute("isSaved", isSaved);
        }

        return "haber-detay";
    }

    @PostMapping("/haber/{id}/yorum")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal CustomUserDetails user,
                             RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/giris";
        }

        CommentCreateRequest request = new CommentCreateRequest();
        request.setArticleId(id);
        request.setContent(content);

        commentService.createComment(request, user.getUserId());
        redirectAttributes.addFlashAttribute("successMsg", "Yorumunuz onay bekliyor.");
        return "redirect:/haber/" + id;
    }

    @PostMapping("/haber/{id}/kaydet")
    public String toggleSaveArticle(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails user,
                                    RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/giris";
        }

        if (savedArticleService.isArticleSaved(user.getUserId(), id)) {
            savedArticleService.unsaveArticle(user.getUserId(), id);
            redirectAttributes.addFlashAttribute("infoMsg", "Makale kayıtlardan kaldırıldı.");
        } else {
            savedArticleService.saveArticle(user.getUserId(), id);
            redirectAttributes.addFlashAttribute("successMsg", "Makale kaydedildi.");
        }

        return "redirect:/haber/" + id;
    }
}
