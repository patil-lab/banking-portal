package com.example.bankingportal;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@OpenAPIDefinition(
		info = @Info(
				title = "Banking Portal API",
				version = "1.0",
				description = "Banking Portal Service",
				license = @License(name = "bankingPortal", url = "localhost"),
				contact = @Contact(url = "localhost", name = "Patil", email = "basavashreepatil@gmail.com")
		))
@EnableAsync
public class BankingPortalApplication {
	@Bean(name="processExecutor")
	public TaskExecutor workExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("Async-");
		threadPoolTaskExecutor.setCorePoolSize(3);
		threadPoolTaskExecutor.setMaxPoolSize(3);
		threadPoolTaskExecutor.setQueueCapacity(600);
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingPortalApplication.class, args);
	}

}
