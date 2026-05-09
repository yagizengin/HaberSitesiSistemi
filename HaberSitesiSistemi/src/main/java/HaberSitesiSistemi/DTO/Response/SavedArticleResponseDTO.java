package HaberSitesiSistemi.DTO.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedArticleResponseDTO {

    private Long saveId;
    private LocalDateTime savedAt;
    private Long userId;
    private Long articleId;
    private ArticleSummaryDTO article;
}
