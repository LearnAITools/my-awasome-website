package org.website;

// Ensure backend/src/main/java is configured as a source root for this module.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.website")
public class MyAwsomeWebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAwsomeWebsiteApplication.class, args);
    }
}
