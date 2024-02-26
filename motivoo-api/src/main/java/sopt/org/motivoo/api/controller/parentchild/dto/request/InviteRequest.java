package sopt.org.motivoo.api.controller.parentchild.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import sopt.org.motivoo.domain.parentchild.dto.request.InviteCommand;

public record InviteRequest (

        @NotBlank(message = "초대코드는 필수 입력 값입니다.")
        @Pattern(regexp = "[A-Za-z0-9]{8}", message = "초대코드 형식에 맞지 않습니다.")
        @Size(max = 8)
        @JsonProperty("invite_code")
        String inviteCode
){

        public InviteCommand toServiceDto() {
                return new InviteCommand(inviteCode);
        }
}