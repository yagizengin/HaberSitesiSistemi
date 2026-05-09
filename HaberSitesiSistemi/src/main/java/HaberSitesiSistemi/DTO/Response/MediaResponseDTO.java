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
public class MediaResponseDTO {

    private Long mediaId;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadedAt;
    private Long articleId;
}
