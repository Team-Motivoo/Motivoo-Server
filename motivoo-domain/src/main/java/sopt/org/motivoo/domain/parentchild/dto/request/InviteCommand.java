package sopt.org.motivoo.domain.parentchild.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteCommand(
        @JsonProperty("invite_code")
        String inviteCode
){

}