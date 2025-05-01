package br.com.fecaf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AutomocaoPagamentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomocaoPagamentosApplication.class, args);
    }

    @Bean
    CommandLineRunner initialization () {
        return args -> {
            System.out.println("API no ar na porta 8080");
        };
    }

}
