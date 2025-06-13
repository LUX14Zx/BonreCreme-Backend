package com.tlfdt.bonrecreme.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class UserConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.users")
    public DataSource usersDataSource() {
        return DataSourceBuilder.create().build();
    }

    // …EntityManagerFactory & TxManager beans…
}

