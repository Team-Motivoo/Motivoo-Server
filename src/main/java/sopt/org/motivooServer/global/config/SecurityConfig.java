package sopt.org.motivooServer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// TODO 인증인가 구현 이후 수정 (현재는 모두 허용해둔 상태)
	private static final String[] AUTH_WHITELIST = {
		"/login/**", "/logout"
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()   // Security 설정 제외
				.authorizeHttpRequests()
			.requestMatchers(AUTH_WHITELIST).permitAll()
			.anyRequest().authenticated()
			.and().build();   // -> 스프링 빈으로 등록, 어플리케이션 실행 시 자동으로 필터에 등록된다!
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins("*")
					.allowedOriginPatterns("*")
					.allowedMethods("*");
			}
		};
	}
}
