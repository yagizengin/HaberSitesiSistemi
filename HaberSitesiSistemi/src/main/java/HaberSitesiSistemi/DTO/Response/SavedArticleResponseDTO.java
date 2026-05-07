package HaberSitesiSistemi.DTO.Response;

import java.sql.Timestamp;

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
    private Timestamp savedAt;
    private Long userId;
    private Long articleId;
    private ArticleSummaryDTO article;
}
