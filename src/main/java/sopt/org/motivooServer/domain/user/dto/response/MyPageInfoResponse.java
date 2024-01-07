package sopt.org.motivooServer.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyPageInfoResponse(
	String name,
	int age
) {

	public static MyPageInfoResponse of(String name, int age) {
		return MyPageInfoResponse.builder()
			.name(name)
			.age(age).build();
	}

	public static MyPageInfoResponse of(String name) {
		return MyPageInfoResponse.builder()
			.name(name).build();
	}
}
