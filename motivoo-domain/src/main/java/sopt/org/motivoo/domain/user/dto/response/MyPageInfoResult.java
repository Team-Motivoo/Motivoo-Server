package sopt.org.motivoo.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResult(
	@JsonProperty("user_nickname") String userNickname,
	@JsonProperty("user_age") Integer userAge,
	@JsonProperty("user_type") String userType
) {

	public static MyPageInfoResult of(User user) {
		return MyPageInfoResult.builder()
			.userNickname(user.getNickname())
			.userAge(user.getAge())
			.userType(user.getType().getValue()).build();
	}
}
