package sopt.org.motivoo.domain.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import sopt.org.motivoo.domain.MotivooDomainRoot;

@Configuration
@EntityScan(basePackageClasses = {MotivooDomainRoot.class})
@EnableJpaRepositories(basePackageClasses = {MotivooDomainRoot.class})
@EnableJpaAuditing
public class JpaAuditingConfig {
}
