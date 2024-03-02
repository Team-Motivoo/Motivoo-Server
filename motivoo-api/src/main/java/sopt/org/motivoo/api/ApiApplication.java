package sopt.org.motivoo.api;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import jakarta.annotation.PostConstruct;
import sopt.org.motivoo.domain.MotivooDomainRoot;
import sopt.org.motivoo.common.MotivooCommonRoot;
import sopt.org.motivoo.external.MotivooExternalRoot;

@SpringBootApplication(scanBasePackageClasses = {
	MotivooCommonRoot.class,
	MotivooDomainRoot.class,
	MotivooExternalRoot.class,
	ApiApplication.class
}, exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@PostConstruct
	public void setTimeZone(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
