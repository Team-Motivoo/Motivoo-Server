package sopt.org.motivoo.api.controller.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import sopt.org.motivoo.domain.auth.dto.request.RefreshCommand;

public record RefreshRequest(
        @JsonProperty("user_id")
        Long userId
) {

        public RefreshCommand toServiceDto() {
                return new RefreshCommand(userId);
        }

}
