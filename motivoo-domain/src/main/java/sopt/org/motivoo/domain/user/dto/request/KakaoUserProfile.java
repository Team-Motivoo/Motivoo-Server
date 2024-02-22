package sopt.org.motivoo.domain.user.dto.request;

import java.util.Map;

import lombok.Getter;
import sopt.org.motivoo.domain.auth.service.OAuth2UserInfo;

@Getter
public class KakaoUserProfile implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoUserProfile(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    } //TODO
    @Override
    public String getNickName() {
        return String.valueOf(attributes.get("nickname"));
    }


}
