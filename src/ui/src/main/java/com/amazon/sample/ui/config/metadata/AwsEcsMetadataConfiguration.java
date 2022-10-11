package com.amazon.sample.ui.config.metadata;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.util.ecs.AwsECSEnvironmentCondition;
import com.amazon.sample.ui.util.ecs.ECSMetadataUtils;
import com.amazon.sample.ui.util.ecs.ECSTaskMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(AwsECSEnvironmentCondition.class)
public class AwsEcsMetadataConfiguration {
    @Bean
    public Metadata metadata() {
        ECSTaskMetadata task = new ECSMetadataUtils().getTaskMetadata().block();

        return new Metadata("ECS")
            .add("az", task.getAvailabilityZone())
            .add("launch-type", task.getLaunchType())
            .add("cluster", task.getCluster())
            .add("family", task.getFamily());
    }
}
