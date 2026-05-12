package HaberSitesiSistemi.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import HaberSitesiSistemi.DTO.Request.CommentCreateRequest;
import HaberSitesiSistemi.DTO.Request.CommentUpdateRequest;
import HaberSitesiSistemi.Model.Article;
import HaberSitesiSistemi.Model.Comment;
import HaberSitesiSistemi.Model.User;
import HaberSitesiSistemi.Repository.ArticleRepository;
import HaberSitesiSistemi.Repository.CommentRepository;
import HaberSitesiSistemi.Repository.UserRepository;
import HaberSitesiSistemi.Util.HtmlSanitizer;
import HaberSitesiSistemi.Exception.ResourceNotFoundException;
import HaberSitesiSistemi.Exception.ForbiddenException;
import HaberSitesiSistemi.Exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public Comment createComment(CommentCreateRequest request, Long userId) {
        log.info("Creating comment on article ID: {} by user ID: {}", request.getArticleId(), userId);

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> {
                    log.warn("Comment creation failed: Article not found with ID: {}", request.getArticleId());
                    return new ResourceNotFoundException("Article", "id", request.getArticleId());
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Comment creation failed: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        Comment comment = new Comment();
        comment.setContent(HtmlSanitizer.sanitize(request.getContent()));
        comment.setArticle(article);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with ID: {}", savedComment.getCommentId());
        return savedComment;
    }

    public Comment updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        log.info("Updating comment ID: {} by user ID: {}", commentId, userId);

        Comment comment = getCommentEntityById(commentId);

        if (!comment.getUser().getUserId().equals(userId)) {
            log.warn("Update denied: User {} is not the owner of comment {}", userId, commentId);
            throw new ForbiddenException("You are not authorized to update this comment");
        }

        comment.setContent(HtmlSanitizer.sanitize(request.getContent()));

        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} updated successfully", commentId);
        return updatedComment;
    }

    public Comment approveComment(Long commentId, Long editorUserId) {
        log.info("Approving comment ID: {} by editor ID: {}", commentId, editorUserId);

        Comment comment = getCommentEntityById(commentId);
        User editor = userRepository.findById(editorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", editorUserId));
        if (!hasRole(editor, "ROLE_EDITOR") && !hasRole(editor, "ROLE_ADMIN")) {
            throw new ForbiddenException("You are not authorized to approve comments");
        }

        if (comment.isApproved()) {
            log.warn("Comment {} is already approved", commentId);
            throw new ConflictException("Comment is already approved");
        }

        comment.setApproved(true);

        Comment approvedComment = commentRepository.save(comment);
        log.info("Comment {} approved successfully", commentId);
        return approvedComment;
    }

    public void deleteComment(Long commentId, Long userId) {
        log.info("Deleting comment ID: {} by user ID: {}", commentId, userId);

        Comment comment = getCommentEntityById(commentId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        boolean isAdmin = hasRole(user, "ROLE_ADMIN");

        if (!comment.getUser().getUserId().equals(userId) && !isAdmin) {
            log.warn("Delete denied: User {} is not the owner of comment {} and not an admin", userId, commentId);
            throw new ForbiddenException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment {} deleted successfully", commentId);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByArticle(Long articleId, Pageable pageable) {
        log.info("Fetching comments for article ID: {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("Article not found with ID: {}", articleId);
                    return new ResourceNotFoundException("Article", "id", articleId);
                });

        return commentRepository.findByArticle(article, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getApprovedComments(Long articleId, Pageable pageable) {
        log.info("Fetching approved comments for article ID: {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("Article not found with ID: {}", articleId);
                    return new ResourceNotFoundException("Article", "id", articleId);
                });

        return commentRepository.findByArticleAndApproved(article, true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getUnapprovedComments(Pageable pageable) {
        log.info("Fetching unapproved comments");
        return commentRepository.findByApproved(false, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getAllComments(Pageable pageable) {
        log.info("Fetching all comments for admin moderation");
        return commentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUser(Long userId, Pageable pageable) {
        log.info("Fetching comments for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        return commentRepository.findByUser(user, pageable);
    }

    private Comment getCommentEntityById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with ID: {}", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> roleName.equals(normalizeRole(role.getName())));
    }

    private String normalizeRole(String roleName) {
        String upper = roleName == null ? "" : roleName.toUpperCase();
        return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
    }
}
