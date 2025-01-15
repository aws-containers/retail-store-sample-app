package com.amazon.sample.orders.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "retail.orders.persistence")
@Getter
@Setter
public class DatabaseProperties {

  @NotEmpty
  private String endpoint;

  @NotEmpty
  private String name;

  @NotEmpty
  private String username;

  @NotEmpty
  private String password;
}
