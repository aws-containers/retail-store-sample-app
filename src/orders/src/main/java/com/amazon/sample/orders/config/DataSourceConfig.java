package com.amazon.sample.orders.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
  prefix = DatabaseProperties.PREFIX,
  name = "provider",
  havingValue = "postgres"
)
@EnableConfigurationProperties(DatabaseProperties.class)
@Slf4j
public class DataSourceConfig {

  @Autowired
  private DatabaseProperties dbProps;

  @Bean
  public DataSource dataSource() {
    log.info("Using postgres database");

    HikariConfig config = new HikariConfig();

    String jdbcUrl = String.format(
      "jdbc:postgresql://%s/%s",
      dbProps.getEndpoint(),
      dbProps.getName()
    );

    log.info("Postgres endpoint: {}", jdbcUrl);

    config.setJdbcUrl(jdbcUrl);
    config.setUsername(dbProps.getUsername());
    config.setPassword(dbProps.getPassword());

    return new HikariDataSource(config);
  }
}
