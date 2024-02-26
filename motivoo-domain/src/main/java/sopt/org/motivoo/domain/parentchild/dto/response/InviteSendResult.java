package sopt.org.motivoo.domain.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteSendResult(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_matched")
        boolean isMatched,

        @JsonProperty("invite_code")
        String inviteCode,

        Long parentchildId
){

}
