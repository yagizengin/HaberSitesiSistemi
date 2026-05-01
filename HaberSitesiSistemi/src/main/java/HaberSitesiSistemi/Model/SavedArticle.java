package HaberSitesiSistemi.Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "saved_articles")
public class SavedArticle {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "save_id")
    private Long save_id;

    @Column(name = "saved_at", nullable = false)
    private Timestamp saved_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @PrePersist
    protected void prePersist() {
        if (this.saved_at == null) {
            this.saved_at = Timestamp.valueOf(LocalDateTime.now());
        }
    }

    public SavedArticle() {
    }

    public Long getSave_id() {
        return save_id;
    }

    public void setSave_id(Long save_id) {
        this.save_id = save_id;
    }

    public Timestamp getSaved_at() {
        return saved_at;
    }

    public void setSaved_at(Timestamp saved_at) {
        this.saved_at = saved_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
