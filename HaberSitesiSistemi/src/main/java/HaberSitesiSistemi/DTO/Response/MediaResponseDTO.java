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
public class MediaResponseDTO {

    private Long mediaId;
    private String fileUrl;
    private String fileType;
    private Timestamp uploadedAt;
    private Long articleId;
}
