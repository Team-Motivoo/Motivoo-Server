package sopt.org.motivoo.api.controller.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.user.dto.response.MyPageInfoResult;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResponse(
	@JsonProperty("user_nickname") String userNickname,
	@JsonProperty("user_age") Integer userAge,
	@JsonProperty("user_type") String userType
) {

	public static MyPageInfoResponse of(MyPageInfoResult result) {
		return MyPageInfoResponse.builder()
			.userNickname(result.userNickname())
			.userAge(result.userAge())
			.userType(result.userType()).build();
	}
}
