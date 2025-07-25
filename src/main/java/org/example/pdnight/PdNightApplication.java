package org.example.pdnight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PdNightApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdNightApplication.class, args);
    }

}
