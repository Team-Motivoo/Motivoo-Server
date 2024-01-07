package sopt.org.motivooServer.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResponse(
	@JsonProperty("user_nickname") String userNickname,
	@JsonProperty("user_age") Integer userAge,
	@JsonProperty("user_type") String userType
) {

	public static MyPageInfoResponse of(User user) {
		return MyPageInfoResponse.builder()
			.userNickname(user.getNickname())
			.userAge(user.getAge()).build();
	}

	public static MyPageInfoResponse ofMain(User user) {
		return MyPageInfoResponse.builder()
			.userNickname(user.getNickname())
			.userType(user.getType().name()).build();
	}
}
