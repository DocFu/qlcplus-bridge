package de.plasmawolke.qlcplusbridge.hap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.qlcplusbridge.Version;
import io.github.hapjava.accessories.HomekitAccessory;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;

public class HomekitService {

    private static final Logger logger = LoggerFactory.getLogger(HomekitService.class);

    private static final File authFile = new File("ax5-bridge-auth.bin");

    private static final String manufacturer = "https://github.com/DocFu/qlcplus-bridge";
    private static final String model = "AX5 Bridge";
    private static final String serialNumber = "1";
    private static final String firmwareRevision = Version.getVersionAndRevision();
    private static final String hardwareRevision = "-";

    private int port;
    private InetAddress address;

    private String pin;

    public HomekitService(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void runWithAccessories(Collection<HomekitAccessory> accessories) throws Exception {

        HomekitServer homekitServer = new HomekitServer(address, port);
        AuthInfo authInfo = createAuthInfo();
        HomekitRoot bridge = homekitServer.createBridge(authInfo, model, 1, manufacturer, model, serialNumber,
                firmwareRevision, hardwareRevision);

        for (HomekitAccessory accessory : accessories) {
            logger.info("Adding HomeKit Accessory: " + accessory);
            bridge.addAccessory(accessory);
        }

        bridge.start();

        authInfo.onChange(state -> {
            try {
                logger.debug("Updating auth file after state has changed.");
                FileOutputStream fileOutputStream = new FileOutputStream(authFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(state);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException e) {
                logger.error("Updating auth file has failed!", e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping homekit service.");
            homekitServer.stop();
        }));

        pin = authInfo.getPin();

        logger.info("Started homekit service successfully on port " + port + ".");

    }

    public void printPin() {
        logger.info("****************");
        logger.info("**    PIN:    **");
        logger.info("** " + pin + " **");
        logger.info("****************");
    }

    private AuthInfo createAuthInfo() throws Exception {
        AuthInfo authInfo;
        if (authFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(authFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                logger.debug("Using state from existing auth file.");
                AuthState authState = (AuthState) objectInputStream.readObject();
                authInfo = new AuthInfo(authState);
            } finally {
                objectInputStream.close();
            }
        } else {
            authInfo = new AuthInfo(createRandomPin());
        }

        return authInfo;
    }

    private String createRandomPin() {
        Random random = new Random();

        String number1 = String.valueOf(random.nextInt(999));
        String number2 = String.valueOf(random.nextInt(99));
        String number3 = String.valueOf(random.nextInt(999));

        number1 = StringUtils.leftPad(number1, 3, "0");
        number2 = StringUtils.leftPad(number2, 2, "0");
        number3 = StringUtils.leftPad(number3, 3, "0");

        return number1 + "-" + number2 + "-" + number3;
    }

}
