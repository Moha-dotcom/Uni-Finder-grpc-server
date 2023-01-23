package com.devBlock.grpc.AppConfig;

import com.devProblems.StudentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
public class ConfigClass {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}