package sopt.org.motivoo.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshCommand(
        @JsonProperty("user_id")
        Long userId
) {

}
