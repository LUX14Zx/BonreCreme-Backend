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
    basePackages = "com.tlfdt.bonrecreme.repository.user",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager"
)
public class UserConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.users")
    public DataSourceProperties userProps() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource userDataSource() {
        return this.userProps().initializeDataSourceBuilder().build();
    }

    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManager(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(userDataSource())
                .packages("com.tlfdt.bonrecreme.model.user")
                .persistenceUnit("user")
                .build();
    }

    @Bean
    public PlatformTransactionManager userTxManager(
            LocalContainerEntityManagerFactoryBean userEntityManager) {

        assert userEntityManager.getObject() != null;
        return new JpaTransactionManager(userEntityManager.getObject());
    }
}
