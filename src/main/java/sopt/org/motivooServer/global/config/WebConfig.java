package sopt.org.motivooServer.global.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sopt.org.motivooServer.global.query.JPAQueryManageInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final JPAQueryManageInterceptor jpaQueryManageInterceptor;

	public WebConfig(@Autowired(required = false) JPAQueryManageInterceptor jpaQueryManageInterceptor) {
		this.jpaQueryManageInterceptor = jpaQueryManageInterceptor;
	}

	@Override
	public void addInterceptors(@NotNull InterceptorRegistry registry) {
		if (jpaQueryManageInterceptor != null) {
			registry.addInterceptor(jpaQueryManageInterceptor)
				.addPathPatterns("/**");
		}
	}
}
