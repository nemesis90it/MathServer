package com.nemesis.mathserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@EnableJpaRepositories("com.nemesis.mathserver.security.jpa.repository")
@EntityScan("com.nemesis.mathserver.security.jpa.entity")
@SpringBootApplication
public class MathServerVaadinApplication {

    public static void main(String[] args) {
        SpringApplication.run(MathServerVaadinApplication.class, args);
    }


//    @Bean
//    public ContextLoaderListener contextLoaderListener(){
//        return new ContextLoaderListener();
//    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

//
//    @Bean
//    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
//        FilterRegistrationBean<RequestContextFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new RequestContextFilter());
//        registration.setName("requestContextFilter");
//        return registration;
//    }

}

