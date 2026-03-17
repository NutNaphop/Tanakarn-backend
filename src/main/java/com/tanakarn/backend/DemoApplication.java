package com.tanakarn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
//	@Bean
//	public CommandLineRunner initData(AccountRepository accountRepository){
//		return args -> {
//			accountRepository.save(new Account("Naphop", 1000000.00));
//			accountRepository.save(new Account("Kimchi", 1000000.00));
//			accountRepository.save(new Account("Sushi", 1000000.00));
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
