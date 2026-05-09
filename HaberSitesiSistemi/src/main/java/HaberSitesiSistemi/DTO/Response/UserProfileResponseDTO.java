package HaberSitesiSistemi.DTO.Response;

import java.time.LocalDateTime;
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
public class UserProfileResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
    private Set<String> roles;
    private List<ArticleSummaryDTO> recentArticles;
    private List<CommentResponseDTO> recentComments;
}
