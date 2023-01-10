/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Profile("mysql")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "readerEntityManagerFactory",
        transactionManagerRef = "readerTransactionManager",
        includeFilters = @ComponentScan.Filter(ReadOnlyRepository.class),
        basePackages = {
                "com.amazon.sample.orders"
        }
)
public class ReaderConfig {

    @Bean(name = "readerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.reader")
    public DataSource customerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "readerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("readerDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.amazon.sample.orders")
                .persistenceUnit("reader")
                .build();
    }

    @Bean(name = "readerTransactionManager")
    public PlatformTransactionManager customerTransactionManager(
            @Qualifier("readerEntityManagerFactory") EntityManagerFactory customerEntityManagerFactory
    ) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}
