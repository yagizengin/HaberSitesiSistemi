package HaberSitesiSistemi.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorDTO {

    private String field;
    private Object rejectedValue;
    private String message;
}
