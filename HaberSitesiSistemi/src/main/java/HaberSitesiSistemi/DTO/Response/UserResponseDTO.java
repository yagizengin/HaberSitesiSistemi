package HaberSitesiSistemi.DTO.Response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long userId;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;
    private Set<String> roles;
}
