package HaberSitesiSistemi.Controller.PageController;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Category;
import HaberSitesiSistemi.Service.ArticleService;
import HaberSitesiSistemi.Service.CategoryService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String homePage(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        Page<Article> articlePage = articleService.getAllArticles(PageRequest.of(0, 12, Sort.by("publishedAt").descending()));
        List<Article> articles = articlePage.getContent();

        // Split articles for the grid layout
        Article heroArticle = articles.isEmpty() ? null : articles.get(0);
        List<Article> leftArticles = articles.size() > 1 ? articles.subList(1, Math.min(4, articles.size())) : List.of();
        List<Article> rightArticles = articles.size() > 4 ? articles.subList(4, Math.min(6, articles.size())) : List.of();
        List<Article> bottomArticles = articles.size() > 6 ? articles.subList(6, articles.size()) : List.of();

        model.addAttribute("categories", categories);
        model.addAttribute("heroArticle", heroArticle);
        model.addAttribute("leftArticles", leftArticles);
        model.addAttribute("rightArticles", rightArticles);
        model.addAttribute("bottomArticles", bottomArticles);

        return "index";
    }

    @GetMapping("/kategori/{id}")
    public String categoryPage(@PathVariable Long id,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        List<Category> categories = categoryService.getAllCategories();
        Category category = categoryService.getCategoryById(id);
        Page<Article> articlePage = articleService.getArticlesByCategory(id,
                PageRequest.of(page, 12, Sort.by("publishedAt").descending()));

        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());

        return "kategori";
    }

    @GetMapping("/ara")
    public String searchPage(@RequestParam(required = false, defaultValue = "") String q,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("query", q);

        if (!q.isBlank()) {
            Page<Article> results = articleService.searchArticles(q,
                    PageRequest.of(page, 12, Sort.by("publishedAt").descending()));
            model.addAttribute("articles", results.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", results.getTotalPages());
            model.addAttribute("totalResults", results.getTotalElements());
        }

        return "arama";
    }

    @GetMapping("/hakkimizda")
    public String hakkimizdaPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Hakkımızda");
        return "hakkimizda";
    }

    @GetMapping("/iletisim")
    public String iletisimPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "İletişim");
        return "iletisim";
    }

    @GetMapping("/gizlilik")
    public String gizlilikPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Gizlilik Politikası");
        return "gizlilik";
    }

    @GetMapping("/kullanim")
    public String kullanimPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Kullanım Koşulları");
        return "kullanim";
    }
}
