package de.plasmawolke.qlcplusbridge;

import java.util.ArrayList;
import java.util.Arrays;
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

        List<MyLight> ax5Group1 = new ArrayList<MyLight>();
        ax5Group1.add(new MyLight(3, "Baulicht", 1));

        List<MyLight> ax5Group2 = new ArrayList<MyLight>();
        ax5Group2.add(new MyLight(4, "HL", 8));
        ax5Group2.add(new MyLight(5, "HR", 15));
        ax5Group2.add(new MyLight(6, "VR", 22));
        ax5Group2.add(new MyLight(7, "VL", 29));

        List<MyLight> ax5Group3 = new ArrayList<MyLight>();
        ax5Group3.add(new MyLight(8, "WTH 1", 36));
        ax5Group3.add(new MyLight(9, "WTH 2", 43));
        ax5Group3.add(new MyLight(10, "WTH 3", 50));
        ax5Group3.add(new MyLight(11, "WTH 4", 57));

        accessories.addAll(ax5Group1);
        accessories.addAll(ax5Group2);
        accessories.addAll(ax5Group3);

        // pseudo-accessory for strobe light
        accessories.add(new MyStrobeLight(101, "Technikblitz", ax5Group1));
        accessories.add(new MyStrobeLight(102, "Feuerblitz", ax5Group2));
        accessories.add(new MyStrobeLight(103, "Wegblitz", ax5Group3));

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

        homekitService.printPin();

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
