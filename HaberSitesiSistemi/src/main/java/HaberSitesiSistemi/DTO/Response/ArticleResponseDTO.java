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
public class ArticleResponseDTO {

    private Long articleId;
    private String title;
    private String content;
    private boolean published;
    private int viewCount;
    private LocalDateTime publishedAt;
    private Long categoryId;
    private String categoryName;
    private Long authorId;
    private String authorUsername;
}
