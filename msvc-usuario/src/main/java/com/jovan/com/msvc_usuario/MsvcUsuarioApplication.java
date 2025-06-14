package com.jovan.com.msvc_usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@SpringBootApplication
public class MsvcUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcUsuarioApplication.class, args);
	}

}
