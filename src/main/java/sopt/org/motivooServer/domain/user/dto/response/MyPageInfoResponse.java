package sopt.org.motivooServer.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import sopt.org.motivooServer.domain.user.entity.User;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResponse(
	String name,
	int age
) {

	public static MyPageInfoResponse of(User user) {
		return MyPageInfoResponse.builder()
			.name(user.getUsername())
			.age(user.getAge()).build();
	}

	public static MyPageInfoResponse ofMain(User user) {
		return MyPageInfoResponse.builder()
			.name(user.getUsername()).build();
	}
}
