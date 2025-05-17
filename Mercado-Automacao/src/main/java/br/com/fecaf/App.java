package br.com.fecaf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.stripe.*;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    //Inicia a aplicação
    @Bean
    CommandLineRunner initialization () {
        return args -> {
            System.out.println("O servidor está no ar !");
        };
    }
}
