package HaberSitesiSistemi.Service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.TagCreateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Tag;
import HaberSitesiSistemi.Repository.ArticleRepository;
import HaberSitesiSistemi.Repository.TagRepository;
import HaberSitesiSistemi.Util.HtmlSanitizer;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    public Tag createTag(TagCreateRequest request) {
        log.info("Creating tag: '{}'", request.getName());
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Tag name already exists");
        }
        Tag tag = new Tag();
        tag.setName(HtmlSanitizer.sanitize(request.getName()));
        Tag saved = tagRepository.save(tag);
        log.info("Tag created with ID: {}", saved.getTagId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Set<Tag> getTagsByArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        return article.getTags();
    }

    public Article addTagToArticle(Long articleId, Long tagId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        article.getTags().add(tag);
        return articleRepository.save(article);
    }

    public Article removeTagFromArticle(Long articleId, Long tagId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        article.getTags().remove(tag);
        return articleRepository.save(article);
    }
}
