package sopt.org.motivoo.common.query;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class JPAQueryManageConfig {

    @Bean
    @Profile({"local", "test"})
    public JPAQueryManageInterceptor jpaQueryManageInterceptor() {
        return new JPAQueryManageInterceptor(new JPAQueryInspector());
    }
}
