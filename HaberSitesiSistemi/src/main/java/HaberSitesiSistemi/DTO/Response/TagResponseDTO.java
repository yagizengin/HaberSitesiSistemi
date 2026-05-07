package HaberSitesiSistemi.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDTO {

    private Long tagId;
    private String name;
    private long articleCount;
}
