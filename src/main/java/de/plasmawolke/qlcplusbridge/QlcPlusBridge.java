package de.plasmawolke.qlcplusbridge;

import com.beust.jcommander.JCommander;
import de.plasmawolke.qlcplusbridge.hap.HomekitService;
import de.plasmawolke.qlcplusbridge.qlc.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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


        VirtualConsoleClient virtualConsoleClient = new VirtualConsoleClient(appArguments.getQlcPlusWebHost(), appArguments.getQlcPlusWebPort());
        List<VirtualConsoleButton> buttons = null;
        try {
            buttons = virtualConsoleClient.collectButtons();
        } catch (NoButtonsFoundException e) {
            exitWithError("No buttons have been marked for export on QLC+ Virtual Console.  Mark some (by using blue button font color) and restart bridge.");
        } catch (VirtualConsoleUnavailableException e) {
            exitWithError("QLC+ Virtual Console seems not to be accessible on '" + e.getUrl() + "'. Is QLC+ started with web option: 'qlcplus -w'?");
        }

        VirtualConsoleWebSocketClient virtualConsoleWebSocketClient = null;
        try {
            virtualConsoleWebSocketClient = new VirtualConsoleWebSocketClient(appArguments.getQlcPlusWebHost(), appArguments.getQlcPlusWebPort(), buttons);
            virtualConsoleWebSocketClient.connect();
        } catch (Exception e) {
            exitWithError("Error on socket: " + e.getMessage());
        }

        HomekitService homekitService = new HomekitService(appArguments.getPort());
        try {
            homekitService.runWithAccessories(buttons);
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " + e.getMessage());
        }


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
