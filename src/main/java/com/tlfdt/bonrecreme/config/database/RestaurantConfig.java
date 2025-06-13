package com.tlfdt.bonrecreme.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class RestaurantConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.restaurant")
    public DataSource restaurantDataSource() {
        return DataSourceBuilder.create().build();
    }

    // …EntityManagerFactory & TxManager beans…
}

