package sopt.org.motivooServer.domain.auth.service;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getNickName();
}
