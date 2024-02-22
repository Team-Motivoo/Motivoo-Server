package sopt.org.motivoo.api.controller.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.parentchild.dto.response.MatchingResult;

@Builder
public record MatchingResponse (
        @JsonProperty("is_matched")
        boolean isMatched,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("opponent_user_id")
        Long opponentUserId
){

        public static MatchingResponse of(MatchingResult result) {
                return MatchingResponse.builder()
                        .isMatched(result.isMatched())
                        .userId(result.userId())
                        .opponentUserId(result.opponentUserId()).build();
        }
}
