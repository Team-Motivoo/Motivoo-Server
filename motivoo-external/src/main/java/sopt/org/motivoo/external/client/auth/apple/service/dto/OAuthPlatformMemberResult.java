package sopt.org.motivoo.external.client.auth.apple.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OAuthPlatformMemberResult (
    @JsonProperty("platform_id")
    String platformId,
    String email
){

}
