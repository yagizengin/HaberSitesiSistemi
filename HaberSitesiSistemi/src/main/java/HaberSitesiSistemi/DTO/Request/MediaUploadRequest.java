package HaberSitesiSistemi.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadRequest {

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private String fileType;

    @NotNull(message = "Article ID is required")
    private Long articleId;
}
