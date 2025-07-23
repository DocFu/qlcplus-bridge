package de.plasmawolke.qlcplusbridge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import de.plasmawolke.qlcplusbridge.hap.HomekitService;
import de.plasmawolke.qlcplusbridge.simdesk.DmxStateStore;
import de.plasmawolke.qlcplusbridge.simdesk.SimpleDeskWebSocketClient;
import io.github.hapjava.accessories.HomekitAccessory;

public class QlcPlusBridge {

    private static final Logger logger = LoggerFactory.getLogger(QlcPlusBridge.class);

    public static void main(String[] args) {

        logger.info("Running QLC+ Bridge " + Version.getVersionAndRevision() + "...");

        AppArguments appArguments = new AppArguments();
        JCommander commander = JCommander.newBuilder()
                .addObject(appArguments)
                .build();
        try {
            commander.parse(args);
        } catch (Exception e) {
            commander.usage();
            exitWithError("Invalid command line arguments detected: " + e.getMessage());
        }

        SimpleDeskWebSocketClient simpleDeskWebSocketClient = getSimpleDeskWebSocketClient(appArguments);
        DmxStateStore.getInstance().setSimpleDeskWebSocketClient(simpleDeskWebSocketClient);

        List<HomekitAccessory> accessories = new ArrayList<HomekitAccessory>();
        accessories.add(new MyLight(3, "Baulicht", 1));

        HomekitService homekitService = new HomekitService(Util.getInetAddress(appArguments.getAddress()),
                appArguments.getPort());
        try {
            homekitService.runWithAccessories(accessories);
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " + e.getMessage());
        }

        try {
            simpleDeskWebSocketClient.connect();
        } catch (Exception e) {
            exitWithError("Error while connecting to QLC+ WebSocket: " + e.getMessage());
        }

    }

    private static SimpleDeskWebSocketClient getSimpleDeskWebSocketClient(AppArguments appArguments) {
        SimpleDeskWebSocketClient simpleDeskWebSocketClient = null;
        try {
            simpleDeskWebSocketClient = new SimpleDeskWebSocketClient(appArguments.getQlcPlusWebHost(),
                    appArguments.getQlcPlusWebPort());
        } catch (Exception e) {
            exitWithError("Error on socket: " + e.getMessage());
        }
        return simpleDeskWebSocketClient;
    }

    private static void exitWithError(String message) {
        if (StringUtils.isBlank(message)) {
            logger.error("Exiting with undefined error.");
        } else {
            logger.error(message);
        }

        logger.info("Exiting with error.");
        System.exit(1);
    }

}
