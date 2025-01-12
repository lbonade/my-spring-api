package fr.lbonade.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.support.WebStack;

@SpringBootApplication
@EnableJpaAuditing
@EnableHypermediaSupport(stacks = WebStack.WEBMVC, type = EnableHypermediaSupport.HypermediaType.HAL)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
