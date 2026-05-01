package HaberSitesiSistemi.Model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Table(name = "media")
@Entity
public class Media {

    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Id
    @Column(name = "media_id")
    private Long media_id;

    @Column(name = "file_url", nullable = false)
    private String file_url;

    @Column(name = "file_type")
    private String file_type;

    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploaded_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
    
    @PrePersist
    protected void prePersist() {
        this.uploaded_at = Timestamp.valueOf(java.time.LocalDateTime.now());
    }

    public Media() {
    }

    public Long getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Long media_id) {
        this.media_id = media_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public Timestamp getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(Timestamp uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
