package sopt.org.motivooServer.global.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI publicAPI() {
		Info info = new Info()
			.title("Motivoo Server")
			.description("Motivoo API Document")
			.version("1.0.0");

		//TODO JWT 토큰 인증을 위한 SecurityScheme 설정 추가

		return new OpenAPI()
			.components(new Components())
			.info(info);
	}
}
