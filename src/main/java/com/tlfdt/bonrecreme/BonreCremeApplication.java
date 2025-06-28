package com.tlfdt.bonrecreme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The main entry point for the BonreCreme application.
 *
 * This class uses {@link SpringBootApplication} to enable auto-configuration,
 * component scanning, and property support.
 *
 * It also uses {@link EnableCaching} to activate Spring's caching abstraction layer,
 * allowing for the use of caching annotations like @Cacheable, @CachePut, and @CacheEvict
 * throughout the application.
 */
@SpringBootApplication
@EnableCaching
public class BonreCremeApplication {

    /**
     * The main method which serves as the entry point for the Java Virtual Machine (JVM)
     * to start the application. It delegates to Spring Boot's {@link SpringApplication}
     * class to bootstrap the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(BonreCremeApplication.class, args);
    }

}