package sopt.org.motivoo.api.controller.user.apple;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuthPlatformMemberResponse {

    private String platformId;
    private String email;
}
