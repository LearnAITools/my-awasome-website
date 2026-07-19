package org.website;

// Ensure backend/src/main/java is configured as a source root for this module.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "org.website")
@EnableScheduling
public class MyAwsomeWebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAwsomeWebsiteApplication.class, args);
    }
}
