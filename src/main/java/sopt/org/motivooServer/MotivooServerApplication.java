package sopt.org.motivooServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MotivooServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotivooServerApplication.class, args);
	}

}
