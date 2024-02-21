package sopt.org.motivoo.api.controller.parentchild.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.parentchild.dto.request.InviteCommand;

public record InviteRequest (
        @JsonProperty("invite_code")
        String inviteCode
){

        public InviteCommand toServiceDto() {
                return new InviteCommand(inviteCode);
        }

}