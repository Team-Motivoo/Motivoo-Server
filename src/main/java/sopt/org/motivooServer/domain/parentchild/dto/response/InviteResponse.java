package sopt.org.motivooServer.domain.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteResponse (
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_matched")
        boolean isMatched
){

}
