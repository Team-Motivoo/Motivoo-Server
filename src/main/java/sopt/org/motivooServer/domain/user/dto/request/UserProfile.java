package sopt.org.motivooServer.domain.user.dto.request;

import lombok.Getter;
import sopt.org.motivooServer.domain.user.dto.oauth.OAuth2UserInfo;

import java.util.Map;

@Getter
public class UserProfile implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public UserProfile(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }


    @Override
    public String getNickName() {
        return String.valueOf(attributes.get("nickname"));
    }


}
