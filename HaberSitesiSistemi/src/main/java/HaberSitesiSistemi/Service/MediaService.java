package HaberSitesiSistemi.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Media;
import HaberSitesiSistemi.Repository.ArticleRepository;
import HaberSitesiSistemi.Repository.MediaRepository;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ArticleRepository articleRepository;
    private final Path uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    public MediaService(
            MediaRepository mediaRepository,
            ArticleRepository articleRepository,
            @Value("${app.upload.dir:uploads/media}") String uploadPath) {
        this.mediaRepository = mediaRepository;
        this.articleRepository = articleRepository;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    public Media uploadMedia(MultipartFile file, Long articleId) {
        log.info("Uploading media for article ID: {}", articleId);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    "File type not allowed. Allowed types: jpg, png, gif, webp");
        }

        Article article = null;
        if (articleId != null) {
            article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        }

        String extension = "";
        switch (contentType.toLowerCase()) {
            case "image/jpeg": extension = ".jpg"; break;
            case "image/png":  extension = ".png"; break;
            case "image/gif":  extension = ".gif"; break;
            case "image/webp": extension = ".webp"; break;
        }
        String safeFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetPath = uploadDir.resolve(safeFilename).normalize();
            if (!targetPath.startsWith(uploadDir)) {
                throw new BadRequestException("Invalid file path");
            }
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file: {}", safeFilename, e);
            throw new RuntimeException("Failed to store file", e);
        }

        String fileUrl = "/uploads/media/" + safeFilename;
        Media media = new Media();
        media.setFileUrl(fileUrl);
        media.setFileType(contentType);
        media.setArticle(article);

        Media saved = mediaRepository.save(media);
        log.info("Media uploaded: ID={}, URL={}", saved.getMediaId(), fileUrl);
        return saved;
    }

    public void deleteMedia(Long mediaId) {
        log.info("Deleting media ID: {}", mediaId);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media", "id", mediaId));

        String fileUrl = media.getFileUrl();
        if (fileUrl != null) {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            try {
                Path filePath = uploadDir.resolve(filename).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Failed to delete file from disk: {}", filename, e);
            }
        }

        mediaRepository.delete(media);
        log.info("Media {} deleted successfully", mediaId);
    }

    public void deleteMediaByArticleId(Long articleId) {
        log.info("Deleting media for article ID: {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        List<Media> mediaList = mediaRepository.findByArticle(article);

        for (Media media : mediaList) {
            deleteMedia(media.getMediaId());
        }
    }

    public Media addMediaToArticle(Long mediaId, Long articleId) {
        log.info("Associating media {} with article {}", mediaId, articleId);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media", "id", mediaId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        media.setArticle(article);
        return mediaRepository.save(media);
    }

    @Transactional(readOnly = true)
    public List<Media> getArticleMedia(Long articleId) {
        log.info("Fetching media for article ID: {}", articleId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        return mediaRepository.findByArticle(article);
    }

    @Transactional(readOnly = true)
    public Media getMediaById(Long mediaId) {
        log.info("Fetching media by ID: {}", mediaId);
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media", "id", mediaId));
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Media> getMediaByUrl(String fileUrl) {
        log.info("Fetching media by URL: {}", fileUrl);
        return mediaRepository.findByFileUrl(fileUrl);
    }
}
