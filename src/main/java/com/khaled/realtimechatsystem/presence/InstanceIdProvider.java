package com.khaled.realtimechatsystem.presence;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InstanceIdProvider {
    private static final String instanceId = UUID.randomUUID().toString();

    public String getInstanceId() {
        return instanceId;
    }
}
