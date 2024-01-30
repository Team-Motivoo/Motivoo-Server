package sopt.org.motivooServer.global.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import sopt.org.motivooServer.domain.auth.config.JwtTokenProvider;

/**
 * Sentry 로그 수집 시 해당 AOP를 이용하여 트랜잭션 및 이벤트 적용
 */
@Getter
public class RequestApiInfo {

	private String userId;
	// private String userName;

	private String httpMethod;
	private String url;
	private String name;

	private final Map<String, String> header = new HashMap<>();
	private final Map<String, String> parameters = new HashMap<>();
	private Map<String, String> body = new HashMap<>();

	private String ipAddress;
	private final String dateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	// Access Token에서 회원정보 추출
	private void setUser() {
		this.userId = JwtTokenProvider.getAuthenticatedUser().toString();
		// this.userName =
	}

	// Request에서 Header 추출
	private void setHeader(HttpServletRequest request) {
		final Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			final String headerName = headerNames.nextElement();
			this.header.put(headerName, request.getHeader(headerName));
		}
	}

	// Request에서 IP주소 추출
	private void setIpAddress(HttpServletRequest request) {
		this.ipAddress = Optional.of(request)
			.map(httpServletRequest -> Optional.ofNullable(request.getHeader("X-Forwarded-For"))
				.orElse(Optional.ofNullable(request.getHeader("Proxy-Client-IP"))
					.orElse(Optional.ofNullable(request.getHeader("WL-Proxy-Client-IP"))
						.orElse(Optional.ofNullable(request.getHeader("HTTP_CLIENT_IP"))
							.orElse(Optional.ofNullable(request.getHeader("HTTP_X_FORWARDED_FOR"))
								.orElse(request.getRemoteAddr())))))).orElse(null);
	}

	// API 정보 추출
	private void setApiInfo(JoinPoint joinPoint, Class joinClass) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		final Method method = signature.getMethod();
		final RequestMapping requestMapping = (RequestMapping) joinClass.getAnnotation(RequestMapping.class);
		final String baseUrl = requestMapping.value()[0];

		// Annotation 기준으로 포인트 캐치
		Stream.of(GetMapping.class, PutMapping.class, PostMapping.class, DeleteMapping.class, RequestMapping.class)
			.filter(method::isAnnotationPresent)
			.findFirst()
			.ifPresent(mappingClass -> {
				final Annotation annotation = method.getAnnotation(mappingClass);
				try {
					final String[] methodUrl = (String[]) mappingClass.getMethod("value").invoke(annotation);
					this.httpMethod = (mappingClass.getSimpleName().replace("Mapping", "")).toUpperCase();
					this.url = String.format("%s%s", baseUrl, methodUrl.length > 0 ? methodUrl[0] : "");
					this.name = (String) mappingClass.getMethod("name").invoke(annotation);
				} catch (Exception e) {
					// TODO 하위 예외 클래스로 변경
					e.printStackTrace();
				}
			});
	}

	// Request Body와 Parameter 추출
	private void setInputStream(JoinPoint joinPoint, ObjectMapper objectMapper) {
		try {
			final CodeSignature signature = (CodeSignature) joinPoint.getSignature();
			final String[] parameterNames = signature.getParameterNames();
			final Object[] args = joinPoint.getArgs();

			for (int i=0; i<parameterNames.length; i++) {
				if (parameterNames[i].equals("request")) {
					this.body = objectMapper.convertValue(args[i], new TypeReference<Map<String, String>>(){});
				} else {
					this.parameters.put(parameterNames[i], objectMapper.writeValueAsString(args[i]));
				}
			}
		} catch (Exception e) {
			// TODO 하위 예외 클래스로 변경
			e.printStackTrace();
		}
	}

	public RequestApiInfo(JoinPoint joinPoint, Class joinClass, ObjectMapper objectMapper) {
		try {
			final HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			setHeader(request);
			setIpAddress(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			setUser();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			setApiInfo(joinPoint, joinClass);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			setInputStream(joinPoint, objectMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}