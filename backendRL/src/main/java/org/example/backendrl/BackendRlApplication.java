package org.example.backendrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendRlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendRlApplication.class, args);
    }

}
