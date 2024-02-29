package sopt.org.motivoo.external.client.auth;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = FeignClientConfig.class)
public class FeignClientConfig {

}