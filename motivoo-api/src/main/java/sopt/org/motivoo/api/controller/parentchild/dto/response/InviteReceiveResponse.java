package sopt.org.motivoo.api.controller.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.parentchild.dto.response.InviteReceiveResult;

@Builder
public record InviteReceiveResponse(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("opponent_user_id")
        Long opponentUserId,

        @JsonProperty("is_matched")
        boolean isMatched
){

        public static InviteReceiveResponse of(InviteReceiveResult result) {
                return InviteReceiveResponse.builder()
                        .userId(result.userId())
                        .opponentUserId(result.opponentUserId())
                        .isMatched(result.isMatched()).build();
        }
}
