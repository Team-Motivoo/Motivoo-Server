package sopt.org.motivoo.external.client.auth;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import sopt.org.motivoo.external.MotivooExternalRoot;

@Configuration
@EnableFeignClients(basePackageClasses = MotivooExternalRoot.class)
public class FeignClientConfig {

}