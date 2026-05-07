package HaberSitesiSistemi.DTO.Response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String username;
    private Set<String> roles;
}
