package com.amazon.sample.ui.util.ecs;

import lombok.Data;

@Data
public class ECSTaskMetadata {
    private String cluster;

    private String family;

    private String taskARN;

    private String availabilityZone;

    private String launchType;
}
