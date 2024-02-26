package sopt.org.motivooServer.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshRequest(
        @JsonProperty("user_id")
        Long userId
) {

}
