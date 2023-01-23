package com.devBlock.grpc;

import com.devProblems.StudentServiceGrpc;
import com.devProblems.UniversityInfo;
import io.grpc.*;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@SpringBootApplication
public class GrpcSpringBootExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcSpringBootExampleApplication.class, args);
    }
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }




}
