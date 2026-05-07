package HaberSitesiSistemi.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePublishRequest {

    @NotNull(message = "Publisher ID is required")
    private Long publisherId;
}
