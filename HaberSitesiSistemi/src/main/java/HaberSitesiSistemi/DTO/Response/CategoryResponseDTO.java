package HaberSitesiSistemi.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private Long categoryId;
    private String name;
    private String description;
    private boolean active;
    private long articleCount;
}
