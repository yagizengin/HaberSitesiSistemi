package HaberSitesiSistemi.DTO.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleListResponseDTO {

    private List<ArticleSummaryDTO> articles;
    private PaginationDTO pagination;
}
