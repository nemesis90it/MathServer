package com.nemesis.mathserver.mathserverboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class MathServerBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MathServerBootApplication.class, args);
    }

}
