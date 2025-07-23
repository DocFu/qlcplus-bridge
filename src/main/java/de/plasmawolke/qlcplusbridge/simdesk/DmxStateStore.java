package de.plasmawolke.qlcplusbridge.simdesk;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmxStateStore {

    private static final Logger logger = LoggerFactory.getLogger(DmxStateStore.class);

    private static final DmxStateStore INSTANCE = new DmxStateStore();
    private final ConcurrentHashMap<Integer, Short> store = new ConcurrentHashMap<>();

    private SimpleDeskWebSocketClient simpleDeskWebSocketClient;

    private DmxStateStore() {
    }

    public void setSimpleDeskWebSocketClient(SimpleDeskWebSocketClient simpleDeskWebSocketClient) {
        this.simpleDeskWebSocketClient = simpleDeskWebSocketClient;
    }

    public static DmxStateStore getInstance() {
        return INSTANCE;
    }

    public void put(int channelId, short value) {
        logger.info("Put value {} for channel {}", value, channelId);

        if (simpleDeskWebSocketClient != null) {
            simpleDeskWebSocketClient.sendDmxValue(channelId, value);
        }
        store.put(channelId, value);
    }

    public Short get(int channelId) {
        return store.get(channelId);
    }

}
