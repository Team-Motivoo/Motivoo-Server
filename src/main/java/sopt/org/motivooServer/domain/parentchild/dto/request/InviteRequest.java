package sopt.org.motivooServer.domain.parentchild.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteRequest (
        @JsonProperty("invite_code")
        String inviteCode
){

}