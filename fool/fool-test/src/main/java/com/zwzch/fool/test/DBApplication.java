package com.zwzch.fool.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "com.zwzch.fool.test")
@ImportResource({"classpath*:mapper.xml",
        "classpath*:mapper-config.xml"})
public class DBApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DBApplication.class);
        springApplication.run();
    }
}
