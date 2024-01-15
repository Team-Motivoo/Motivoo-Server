package sopt.org.motivooServer.global.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI publicAPI() {
		Info info = new Info()
			.title("Motivoo Server API 명세서")
			.description("Motivoo API Document")
			.version("v1.0.0");

		//TODO JWT 토큰 인증을 위한 SecurityScheme 설정 추가
		// Security 스키마 설정
		String jwtSchemeName = "jwtAuth";

		// Security 요청 설정
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

		SecurityScheme bearerAuth = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name(HttpHeaders.AUTHORIZATION);

		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, bearerAuth);


		return new OpenAPI()
			.components(components)
			// API 마다 Security 인증 컴포넌트 설정
			.addSecurityItem(securityRequirement)
			.info(info);
	}
}
