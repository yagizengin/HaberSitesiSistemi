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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionLog {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long session_id;

    @Column(name = "ip_address")
    private String ip_address;

    @Column(name = "login_time", nullable = false)
    private Timestamp login_time;

    @Column(name = "logout_time")
    private Timestamp logout_time;

    @Column(name = "is_success", nullable = false)
    private boolean is_success;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void prePersist() {
        if (this.login_time == null) {
            this.login_time = Timestamp.valueOf(LocalDateTime.now());
        }
    }
}
