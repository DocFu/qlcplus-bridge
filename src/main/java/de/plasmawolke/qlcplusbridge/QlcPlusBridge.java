package de.plasmawolke.qlcplusbridge;

import com.beust.jcommander.JCommander;
import de.plasmawolke.qlcplusbridge.hap.HomekitService;

import io.github.hapjava.accessories.HomekitAccessory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

        List<HomekitAccessory> accessories = new ArrayList<HomekitAccessory>();
        HomekitService homekitService = new HomekitService(Util.getInetAddress(appArguments.getAddress()),
                appArguments.getPort());
        try {
            homekitService.runWithAccessories(accessories);
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
