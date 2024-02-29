package sopt.org.motivoo.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import sopt.org.motivoo.domain.user.entity.User;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResult(
	String userNickname,
	Integer userAge,
	String userType
) {

	public static MyPageInfoResult of(User user) {
		return MyPageInfoResult.builder()
			.userNickname(user.getNickname())
			.userAge(user.getAge())
			.userType(user.getType().getValue()).build();
	}
}
