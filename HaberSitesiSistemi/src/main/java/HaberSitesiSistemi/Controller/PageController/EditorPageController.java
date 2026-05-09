package HaberSitesiSistemi.Controller.PageController;

import java.util.List;

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

import HaberSitesiSistemi.DTO.Request.ArticleCreateRequest;
import HaberSitesiSistemi.DTO.Request.ArticleUpdateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Security.CustomUserDetails;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CategoryService;
import HaberSitesiSistemi.Service.TagService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/editor")
@RequiredArgsConstructor
public class EditorPageController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Page<Article> articles = articleService.getArticlesByAuthor(
                user.getUserId(), PageRequest.of(page, 20, Sort.by("publishedAt").descending()));

        model.addAttribute("articles", articles.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        return "editor/dashboard";
    }

    @GetMapping("/yeni-makale")
    public String newArticleForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("isEdit", false);
        return "editor/makale-form";
    }

    @PostMapping("/yeni-makale")
    public String createArticle(@RequestParam String title,
                                @RequestParam String content,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) List<Long> tagIds,
                                @RequestParam(required = false) Long coverImageId,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes ra) {
        try {
            ArticleCreateRequest req = new ArticleCreateRequest();
            req.setTitle(title);
            req.setContent(content);
            req.setCategoryId(categoryId);
            req.setTagIds(tagIds);
            req.setCoverImageId(coverImageId);
            articleService.createArticle(req, user.getUserId());
            ra.addFlashAttribute("successMsg", "Makale oluşturuldu.");
            return "redirect:/editor";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/editor/yeni-makale";
        }
    }

    @GetMapping("/makale-duzenle/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("isEdit", true);
        return "editor/makale-form";
    }

    @PostMapping("/makale-duzenle/{id}")
    public String updateArticle(@PathVariable Long id,
                                @RequestParam String title,
                                @RequestParam String content,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) List<Long> tagIds,
                                @RequestParam(required = false) Long coverImageId,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes ra) {
        try {
            ArticleUpdateRequest req = new ArticleUpdateRequest();
            req.setTitle(title);
            req.setContent(content);
            req.setCategoryId(categoryId);
            req.setTagIds(tagIds);
            req.setCoverImageId(coverImageId);
            articleService.updateArticle(id, req, user.getUserId());
            ra.addFlashAttribute("successMsg", "Makale güncellendi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/editor";
    }

    @PostMapping("/makale-yayinla/{id}")
    public String publishArticle(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 RedirectAttributes ra) {
        try {
            articleService.publishArticle(id, user.getUserId());
            ra.addFlashAttribute("successMsg", "Makale yayınlandı.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/editor";
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
        return "redirect:/editor";
    }
}
