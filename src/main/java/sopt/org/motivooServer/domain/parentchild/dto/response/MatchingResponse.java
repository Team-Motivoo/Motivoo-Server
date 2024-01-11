package sopt.org.motivooServer.domain.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchingResponse (
        @JsonProperty("is_matched")
        boolean isMatched,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("opponent_user_id")
        Long opponentUserId
){

}
