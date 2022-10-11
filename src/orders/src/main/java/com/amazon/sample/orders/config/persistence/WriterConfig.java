package com.amazon.sample.orders.config.persistence;

import com.amazon.sample.orders.repositories.ReadOnlyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Profile("mysql")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "writerEntityManagerFactory",
        transactionManagerRef = "writerTransactionManager",
        excludeFilters = @ComponentScan.Filter(ReadOnlyRepository.class),
        basePackages = {
                "com.amazon.sample.orders"
        }
)
public class WriterConfig {

    @Primary
    @Bean(name = "writerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.writer")
    public DataSource customerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "writerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("writerDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.amazon.sample.orders")
                .persistenceUnit("writer")
                .build();
    }

    @Primary
    @Bean(name = "writerTransactionManager")
    public PlatformTransactionManager writerTransactionManager(
            @Qualifier("writerEntityManagerFactory") EntityManagerFactory writerEntityManagerFactory
    ) {
        return new JpaTransactionManager(writerEntityManagerFactory);
    }
}
