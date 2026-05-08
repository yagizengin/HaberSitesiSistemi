package HaberSitesiSistemi.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    private String content;

    @NotNull(message = "Article ID is required")
    private Long articleId;
}
