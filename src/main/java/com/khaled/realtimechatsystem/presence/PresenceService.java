package com.khaled.realtimechatsystem.presence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRegistry presenceRegistry;
    private final InstanceIdProvider instanceIdProvider;

    public void userConnected(String username) {
        presenceRegistry.markOnline(username, instanceIdProvider.getInstanceId());
    }

    public void userDisconnected(String username) {
        presenceRegistry.markOffline(username);
    }

    public String findHostingInstance(String username) {
        return presenceRegistry.getInstanceFor(username);
    }

    public boolean isCurrentInstance(String username) {
        String hostingInstance = findHostingInstance(username);
        return instanceIdProvider.getInstanceId().equals(hostingInstance);
    }
}
