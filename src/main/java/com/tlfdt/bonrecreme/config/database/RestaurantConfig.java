package com.tlfdt.bonrecreme.config.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configures the secondary "restaurant" database connection, including its
 * DataSource, EntityManagerFactory, and TransactionManager.
 * This setup enables the application to connect to a separate database for
 * restaurant-specific data, isolating it from the primary data source.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.tlfdt.bonrecreme.repository.restaurant",
        entityManagerFactoryRef = "restaurantEntityManagerFactory",
        transactionManagerRef = "restaurantTransactionManager"
)
public class RestaurantConfig {

    /**
     * Creates the DataSourceProperties for the restaurant database.
     * These properties (URL, username, password, driver) are bound from the
     * "spring.datasource.restaurant" namespace in the application configuration file.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.restaurant")
    public DataSourceProperties restaurantDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates the DataSource bean for the restaurant database.
     * It uses the configured DataSourceProperties to build and initialize a connection pool.
     */
    @Bean
    public DataSource restaurantDataSource() {
        return restaurantDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Creates the EntityManagerFactory for the restaurant persistence unit.
     * It scans the specified packages for entity classes and links them to the
     * restaurant DataSource.
     *
     * @param builder The EntityManagerFactoryBuilder provided by Spring Boot.
     * @return A configured LocalContainerEntityManagerFactoryBean for the restaurant database.
     */
    @Bean(name = "restaurantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean restaurantEntityManager(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(restaurantDataSource())
                .packages("com.tlfdt.bonrecreme.model.restaurant")
                .persistenceUnit("restaurant") // This name is important for identifying the persistence unit
                .build();
    }

    /**
     * Creates the TransactionManager for the restaurant database.
     * This manager handles transactions for all repository operations configured
     * to use the "restaurantEntityManagerFactory".
     *
     * @param restaurantEntityManagerFactory The EntityManagerFactory for the restaurant data source.
     * @return A JpaTransactionManager configured for the restaurant database.
     */
    @Bean(name = "restaurantTransactionManager")
    public PlatformTransactionManager restaurantTransactionManager(
            @Qualifier("restaurantEntityManagerFactory") LocalContainerEntityManagerFactoryBean restaurantEntityManagerFactory) {
        assert restaurantEntityManagerFactory.getObject() != null;
        return new JpaTransactionManager(restaurantEntityManagerFactory.getObject());
    }
}
