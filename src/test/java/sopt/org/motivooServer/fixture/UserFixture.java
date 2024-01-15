package sopt.org.motivooServer.fixture;

import lombok.val;
import sopt.org.motivooServer.domain.user.entity.SocialPlatform;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.entity.UserType;

/**
 * 테스트코드에서 공통으로 사용할 객체 생성 및 상수 선언을 하기 위한 클래스
 */
public class UserFixture {

	private static final String USER_NICKNAME = "모티부";

	private static final int USER_AGE = 20;
	private static final UserType USER_TYPE = UserType.CHILD;
	private static final String USER_SOCIAL_ID = "1234567social";
	private static final SocialPlatform USER_SOCIAL_PLATFORM = SocialPlatform.KAKAO;

	private static final String USER_NICKNAME2 = "모티부";

	private static final int USER_AGE2 = 45;
	private static final UserType USER_TYPE2 = UserType.PARENT;
	private static final String USER_SOCIAL_ID2 = "1234567social";
	private static final SocialPlatform USER_SOCIAL_PLATFORM2 = SocialPlatform.KAKAO;


	public static User createUser() {
		// Lombok에서 제공하는 final 선언
		val user = User.builderInTest()
			.nickname(USER_NICKNAME)
			.age(USER_AGE)
			.socialId(USER_SOCIAL_ID)
			.socialPlatform(USER_SOCIAL_PLATFORM)
			.type(USER_TYPE).build();
		return user;
	}

	public static User createUserV2() {
		val user = User.builderInTest()
			.nickname(USER_NICKNAME2)
			.age(USER_AGE2)
			.socialId(USER_SOCIAL_ID2)
			.socialPlatform(USER_SOCIAL_PLATFORM2)
			.type(USER_TYPE2).build();
		return user;
	}
}
