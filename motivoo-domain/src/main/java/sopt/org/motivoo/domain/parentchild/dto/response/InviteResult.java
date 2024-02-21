package sopt.org.motivoo.domain.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteResult(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_matched")
        boolean isMatched,

        @JsonProperty("my_invite_code")
        boolean myInviteCode,

        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding
){

}