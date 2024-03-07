package sopt.org.motivoo.external.s3;

import static sopt.org.motivoo.external.s3.S3ExceptionType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.common.advice.BusinessException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum S3BucketDirectory {

	MISSION_PREFIX("mission"),
	TEST_PREFIX("test");

	private final String name;

	public String value() {
		return this.name + "/";
	}

	public static S3BucketDirectory of(String value) {
		return Arrays.stream(S3BucketDirectory.values())
			.filter(prefix -> value.equals(prefix.name))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_BUCKET_PREFIX));
	}
}
