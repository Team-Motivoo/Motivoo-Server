package sopt.org.motivoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sopt.org.motivoo.common.MotivooCommonRoot;
import sopt.org.motivoo.domain.MotivooDomainRoot;
import sopt.org.motivoo.external.MotivooExternalRoot;

@SpringBootApplication(scanBasePackageClasses = {
	MotivooCommonRoot.class,
	MotivooDomainRoot.class,
	MotivooExternalRoot.class,
	BatchApplication.class
})
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}
}