package sopt.org.motivooServer.domain.auth.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshToken {
    private Long id;
    private String refreshToken;

}
