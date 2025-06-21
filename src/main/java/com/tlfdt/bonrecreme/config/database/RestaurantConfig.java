package com.tlfdt.bonrecreme.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.tlfdt.bonrecreme.repository.restaurant",
    entityManagerFactoryRef = "restaurantEntityManagerFactory",
    transactionManagerRef = "restaurantTransactionManager"
)
public class RestaurantConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.restaurant")
    public DataSourceProperties restaurantProps() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource restaurantDataSource() {
        return this.restaurantProps().initializeDataSourceBuilder().build();
    }

    @Bean(name = "restaurantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean restaurantEntityManager(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(restaurantDataSource())
                .packages("com.tlfdt.bonrecreme.model.restaurant")
                .persistenceUnit("restaurant")
                .build();
    }


    @Bean
    public PlatformTransactionManager restaurantTxManager(
            LocalContainerEntityManagerFactoryBean restaurantEntityManager) {

        assert restaurantEntityManager.getObject() != null;
        return new JpaTransactionManager(restaurantEntityManager.getObject());
    }
}
