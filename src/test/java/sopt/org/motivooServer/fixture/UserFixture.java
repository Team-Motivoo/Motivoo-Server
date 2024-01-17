package sopt.org.motivooServer.fixture;

import lombok.val;
import sopt.org.motivooServer.domain.health.dto.request.OnboardingRequest;
import sopt.org.motivooServer.domain.health.entity.Health;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;
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

	private static final int USER_AGE3 =20;
	private static final String USER_NICKNAME3 = "모티부";
	private static final UserType USER_TYPE3 = UserType.CHILD;
	private static final String SOCIAL_ID = "1222222";
	private static final String SOCIAL_ACCESS_TOKEN = "Dfsfnoidnfa";
	private static final String REFRESH_TOKEN = "sdffsfsfdsfd";
	private static final boolean DELETED = false;
	private static final SocialPlatform USER_SOCIAL_PLATFORM3 = SocialPlatform.KAKAO;

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

	public static User createUserV3(){
		val user = User.builder()
				.nickname(USER_NICKNAME3)
				.socialId(SOCIAL_ID)
				.socialPlatform(USER_SOCIAL_PLATFORM3)
				.socialAccessToken(SOCIAL_ACCESS_TOKEN)
				.refreshToken(REFRESH_TOKEN)
				.type(USER_TYPE3)
				.deleted(DELETED)
				.build();
		return user;
	}

	public static User createUserV4(){
		val health = HealthFixture.createHealthInfo();
		val user = health.getUser();
		user.addParentChild(ParentchildFixture.createParentchild());
		return user;
	}

	public static OnboardingRequest createOnboardingRequest(){
		val health = HealthFixture.createHealthInfo();
		return OnboardingRequest.of(health.getUser(), health);
	}
}
