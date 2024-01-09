package sopt.org.motivooServer.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthTokenRequest {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @Builder
    public OauthTokenRequest(String accessToken, String tokenType){
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }
}
