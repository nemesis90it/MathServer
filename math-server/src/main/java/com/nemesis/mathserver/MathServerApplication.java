package com.nemesis.mathserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@SpringBootApplication
public class MathServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MathServerApplication.class, args);
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

