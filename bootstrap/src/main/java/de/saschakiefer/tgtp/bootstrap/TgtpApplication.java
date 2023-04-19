package de.saschakiefer.tgtp.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.saschakiefer.tgtp")
public class TgtpApplication {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(TgtpApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
