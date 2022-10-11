package com.amazon.sample.ui.config.metadata;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.util.ecs.AwsECSEnvironmentCondition;
import com.amazon.sample.ui.util.ecs.MissingAwsECSEnvironmentCondition;
import io.awspring.cloud.context.annotation.ConditionalOnMissingAwsCloudEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingAwsCloudEnvironment
@Conditional(MissingAwsECSEnvironmentCondition.class)
public class DefaultMetadataConfiguration {

    @Bean
    public Metadata metadata() {
        return new Metadata("Local").add("environment", "local");
    }
}
