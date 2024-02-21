package sopt.org.motivoo.api.aop;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.nimbusds.oauth2.sdk.auth.verifier.Hint;

import io.sentry.EventProcessor;
import io.sentry.SentryEvent;
import io.sentry.SentryTransaction;
import io.sentry.SpanStatus;

/**
 * SentryTransaction 적용 시, 로드밸런서에서 다른 트랜잭션까지 등록되는 것을 막기 위해 필터링 구현
 */
@Component
public class SentryEventProcessor implements EventProcessor {

	public static final String TAG_KEY = "type";
	public static final String TAG_VALUE = "request-api";

	// 설정한 태그의 이벤트인 것만 등록
	@Override
	public @Nullable SentryEvent process(@NotNull SentryEvent event, @Nullable Object hint) {
		if (TAG_VALUE.equals(event.getTag(TAG_KEY))) {
			return event;
		}
		return null;
	}

	// 1. 설정한 태그의 트랜잭션이고 2. 트랜잭션의 상태가 OK인 것만 등록
	// @Override
	public @Nullable SentryTransaction process(@NotNull SentryTransaction transaction, @NotNull Hint hint) {
		if (Objects.equals(transaction.getStatus(), SpanStatus.OK) && TAG_VALUE.equals(transaction.getTag(TAG_KEY))) {
			return transaction;
		}
		return null;
	}
}
