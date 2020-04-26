package com.StoreApp.WS;

import com.StoreApp.WS.security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class IT_HAS_BEEN_STARTED extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IT_HAS_BEEN_STARTED.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(IT_HAS_BEEN_STARTED.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringApplicationsContext springApplicationsContext() {
        return new SpringApplicationsContext();
    }

    @Bean(name = "AppProperties")
    public AppProperties getAppProperties() {
        return new AppProperties();
    }

}

