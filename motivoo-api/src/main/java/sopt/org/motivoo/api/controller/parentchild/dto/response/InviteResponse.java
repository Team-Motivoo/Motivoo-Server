package sopt.org.motivoo.api.controller.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteResult;

@Builder
public record InviteResponse (
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_matched")
        boolean isMatched,

        @JsonProperty("my_invite_code")
        boolean myInviteCode,

        @JsonProperty("is_finished_onboarding")
        boolean isFinishedOnboarding
){

        public static InviteResponse of(InviteResult result) {
                return InviteResponse.builder()
                        .userId(result.userId())
                        .isMatched(result.isMatched())
                        .myInviteCode(result.myInviteCode())
                        .isFinishedOnboarding(result.isFinishedOnboarding()).build();
        }
}
