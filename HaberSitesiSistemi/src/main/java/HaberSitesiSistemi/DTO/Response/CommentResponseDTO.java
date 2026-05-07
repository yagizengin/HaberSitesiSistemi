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
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private Timestamp createdAt;
    private boolean approved;
    private Long articleId;
    private Long userId;
    private String username;
}
