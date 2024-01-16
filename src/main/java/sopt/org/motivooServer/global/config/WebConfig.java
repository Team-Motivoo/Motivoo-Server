package sopt.org.motivooServer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sopt.org.motivooServer.global.query.JPAQueryManageInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final JPAQueryManageInterceptor jpaQueryManageInterceptor;

	public WebConfig(JPAQueryManageInterceptor jpaQueryManageInterceptor) {
		this.jpaQueryManageInterceptor = jpaQueryManageInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jpaQueryManageInterceptor)
			.addPathPatterns("/**");
	}
}
