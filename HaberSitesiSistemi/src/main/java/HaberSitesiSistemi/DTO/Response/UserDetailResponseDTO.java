package HaberSitesiSistemi.DTO.Response;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private boolean active;
    private Timestamp createdAt;
    private Set<String> roles;
    private long articleCount;
    private long commentCount;
    private long savedArticleCount;
    private List<ArticleSummaryDTO> articles;
    private List<CommentResponseDTO> comments;
}
