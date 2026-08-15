package com.khaled.realtimechatsystem.presence;

public record ForwardedDelivery(
        String targetInstanceId,
        MessageDeliveryEvent event
) {
}
