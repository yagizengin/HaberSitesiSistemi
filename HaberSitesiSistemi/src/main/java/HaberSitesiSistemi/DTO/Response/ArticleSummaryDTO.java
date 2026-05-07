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
public class ArticleSummaryDTO {

    private Long articleId;
    private String title;
    private String excerpt;
    private String categoryName;
    private String authorUsername;
    private Timestamp publishedAt;
    private int viewCount;
    private String firstMediaUrl;
}
