package de.plasmawolke.qlcplusbridge.hap;


import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleButton;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;

public class HomekitService {

    private static final Logger logger = LoggerFactory.getLogger(HomekitService.class);

    private static final File authFile = new File("qlcplus-bridge-auth.bin");
    private static final int PORT = 9123;

    private static final String manufacturer = "https://github.com/DocFu/qlcplus-bridge";
    private static final String model = "QLC+ Bridge";
    private static final String serialNumber = "0";
    private static final String firmwareRevision = "0.1";
    private static final String hardwareRevision = "-";
    private static final String pin = "732-57-030";


    public void runWithAccessories(Collection<VirtualConsoleButton> buttons) throws Exception {

        HomekitServer homekitServer = new HomekitServer(PORT);
        AuthInfo authInfo = createAuthInfo();
        HomekitRoot bridge = homekitServer.createBridge(authInfo, model, manufacturer, model, serialNumber, firmwareRevision, hardwareRevision);

        for (VirtualConsoleButton vcb : buttons) {
            logger.info("Registering virtual console button at bridge: " + vcb);
            bridge.addAccessory(vcb);
        }

        bridge.start();

        authInfo.onChange(state -> {
            try {
                logger.info("Updating auth file after state has changed.");
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
            logger.info("Stopping homekit server.");
            homekitServer.stop();
        }));


        logger.info("Started successfully with PIN " + pin);

    }


    private AuthInfo createAuthInfo() throws Exception {
        AuthInfo authInfo;
        if (authFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(authFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                logger.info("Using state from existing auth file.");
                AuthState authState = (AuthState) objectInputStream.readObject();
                authInfo = new AuthInfo(authState);
            } finally {
                objectInputStream.close();
            }
        } else {
            authInfo = new AuthInfo(pin);
        }


        return authInfo;
    }


}
