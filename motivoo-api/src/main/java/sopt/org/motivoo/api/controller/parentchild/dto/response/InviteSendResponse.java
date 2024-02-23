package sopt.org.motivoo.api.controller.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteSendResult;

@Builder
public record InviteSendResponse(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_matched")
        boolean isMatched,

        @JsonProperty("invite_code")
        String inviteCode
){

        public static InviteSendResponse of(InviteSendResult result) {
                return InviteSendResponse.builder()
                        .userId(result.userId())
                        .isMatched(result.isMatched())
                        .inviteCode(result.inviteCode()).build();
        }
}
