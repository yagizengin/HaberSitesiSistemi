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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "media")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
