package HaberSitesiSistemi.DTO.Response;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDetailResponseDTO {

    private Long articleId;
    private String title;
    private String content;
    private boolean published;
    private int viewCount;
    private Timestamp publishedAt;
    private CategoryResponseDTO category;
    private UserResponseDTO author;
    private List<TagResponseDTO> tags;
    private List<CommentResponseDTO> comments;
    private List<MediaResponseDTO> media;
}
