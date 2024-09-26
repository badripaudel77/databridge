package com.example.datasyncapp;

import com.example.datasyncapp.services.ProviderService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class DataSyncAppApplication {
    ApplicationContext context;
    static ConfigurableApplicationContext applicationContext;
    private static ProviderService providerService;

    @Value("${server.port}")
    private int port;

    @Value("${com.my.custom.ppt}")
    private String customValue;


    @Autowired
    public DataSyncAppApplication(ApplicationContext context, ProviderService providerService) {
        this.context = context;
        DataSyncAppApplication.providerService = providerService;
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(DataSyncAppApplication.class, args);
        providerService.fetchAndSyncProvidersToMongo();
        providerService.fetchAndSyncProvidersToRedis();
        applicationContext.close();
        log.info(">>> Sync Operation completed >>>");
    }

    @PostConstruct
    public void setUp() {
        log.info(">>> set up post construct >>>");
        log.info("Custom value >> {}", customValue);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            log.info(">>> Printing all the beans : ");
            String[] beanDefinitionNames = context.getBeanDefinitionNames();
            Arrays.sort(beanDefinitionNames);
            for (String bean : beanDefinitionNames) {
                // System.out.println("Bean = " + bean);
            }
        };
    }

}
