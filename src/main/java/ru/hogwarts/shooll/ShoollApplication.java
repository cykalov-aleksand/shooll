package ru.hogwarts.shooll;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ShoollApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoollApplication.class, args);
    }

}
