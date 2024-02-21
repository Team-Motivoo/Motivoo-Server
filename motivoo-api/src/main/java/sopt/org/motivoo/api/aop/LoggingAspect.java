package sopt.org.motivoo.api.aop;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SpanStatus;
import io.sentry.protocol.Message;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception 발생 시 트랜잭션을 적용하고 SentryEvent에 정보를 담아 다시 던지도록 설록
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

	private final ObjectMapper objectMapper;

	@Pointcut("within(sopt.org.motivoo.domain.mission.*Controller) || within(sopt.org.motivooServer.domain.parentchild.*Controller) || within(sopt.org.motivooServer.domain.user.*Controller)")   // 패키지 범위 설정
	public void onRequest() {}

	@Around("onRequest()")
	public Object requestLogging(ProceedingJoinPoint joinPoint) throws Throwable {
		// API의 정보를 담는 클래스
		final RequestApiInfo apiInfo = new RequestApiInfo(joinPoint, joinPoint.getTarget().getClass(), objectMapper);
		final String requestMessage = String.format("%s %s", apiInfo.getHttpMethod(), apiInfo.getUrl());
		final String body = objectMapper.writeValueAsString(apiInfo.getBody());
		final String parameter = objectMapper.writeValueAsString(apiInfo.getParameters());

		// Request 설정
		final Request request = new Request();
		request.setUrl(apiInfo.getUrl());
		request.setMethod(apiInfo.getHttpMethod());
		request.setData(apiInfo.getBody());
		request.setQueryString(apiInfo.getParameters().keySet().stream()
			.map(key -> key + "=" + apiInfo.getParameters().get(key))
			.reduce("", (a, b) -> a + "&" + b)
		);

		// User 설정
		final User user = new User();
		setUserInfo(user, apiInfo);

		// 트랜잭션 설정 & 필터링
		final ITransaction transaction = Sentry.startTransaction(requestMessage, "request-api");
		setTransactionInfo(transaction);
		Sentry.configureScope(scope -> {
			scope.setRequest(request);
			scope.setUser(user);
		});

		try {
			final Object result = joinPoint.proceed(joinPoint.getArgs());
			return result;
		} catch (Exception e) {
			final StringWriter sw = new StringWriter();
			log.error(String.valueOf(sw));

			final String exceptionAsString = sw.toString();

			// Sentry Event 생성 및 설정
			final SentryEvent event = new SentryEvent();
			event.setRequest(request);
			event.setUser(user);
			event.setLevel(SentryLevel.ERROR);
			event.setTransaction(transaction.getName());

			// Event Message 설정
			final Message message = new Message();
			message.setMessage(requestMessage);
			event.setMessage(message);

			// Exception 설정
			final SentryException exception = new SentryException();
			exception.setType(e.getClass().getSimpleName());
			exception.setValue(exceptionAsString);

			event.setExceptions(List.of(exception));
			Sentry.captureEvent(event);   // Sentry로 출력

			throw e;
		} finally {
			// 트랜잭션 close
			transaction.finish();
		}
	}

	private void setUserInfo(User user, RequestApiInfo apiInfo) {
		user.setId(Optional.ofNullable(apiInfo.getUserId()).orElse("Unknown"));
		user.setIpAddress(apiInfo.getIpAddress());
	}

	private void setTransactionInfo(ITransaction transaction) {
		transaction.setTag(SentryEventProcessor.TAG_KEY, SentryEventProcessor.TAG_VALUE);
		transaction.setStatus(SpanStatus.OK);
	}
}
