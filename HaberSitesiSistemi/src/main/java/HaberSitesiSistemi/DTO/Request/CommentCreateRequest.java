package HaberSitesiSistemi.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Article ID is required")
    private Long articleId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
