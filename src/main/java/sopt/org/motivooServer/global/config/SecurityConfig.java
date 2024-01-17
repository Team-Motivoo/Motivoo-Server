package sopt.org.motivooServer.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sopt.org.motivooServer.domain.auth.config.CustomAccessDeniedHandler;
import sopt.org.motivooServer.domain.auth.config.CustomJwtAuthenticationEntryPoint;
import sopt.org.motivooServer.domain.auth.config.JwtAuthenticationFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private static final String[] AUTH_WHITELIST = {
		"/", "/**", "/oauth/**", "/api/**", "/actuator/health","/withdraw", "/mission/**", "/home",
		"/swagger/**", "/swagger-ui/**", "/v3/api-docs/**", "/docs/**", "/swagger-ui.html"
};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.sessionManagement()
				.sessionCreationPolicy(STATELESS)
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(customJwtAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)
				.and()
				.authorizeHttpRequests()
				.requestMatchers(AUTH_WHITELIST).permitAll()
				.anyRequest().authenticated()
				.and().logout(logout -> logout.permitAll()
						.logoutSuccessHandler((request, response, authentication) -> {
							response.setStatus(HttpServletResponse.SC_OK);
						}))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

}