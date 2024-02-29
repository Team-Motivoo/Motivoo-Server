package sopt.org.motivoo.domain.auth.dto.response;

public record OauthTokenResult(
    String accessToken,
    String refreshToken
){
    public String getRefreshToken(){
        return this.refreshToken();
    }
}
