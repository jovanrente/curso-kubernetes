package com.jovan.com.msvc_cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcCursosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcCursosApplication.class, args);
    }
} 