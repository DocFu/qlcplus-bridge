package de.plasmawolke.qlcplusbridge.hap;


import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleButton;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Random;

public class HomekitService {

    private static final Logger logger = LoggerFactory.getLogger(HomekitService.class);

    private static final File authFile = new File("qlcplus-bridge-auth.bin");


    private static final String manufacturer = "https://github.com/DocFu/qlcplus-bridge";
    private static final String model = "QLC+ Bridge";
    private static final String serialNumber = "0";
    private static final String firmwareRevision = "0.1";
    private static final String hardwareRevision = "-";



    private int port;


    public HomekitService(int port){
        this.port = port;
    }


    public void runWithAccessories(Collection<VirtualConsoleButton> buttons) throws Exception {

        HomekitServer homekitServer = new HomekitServer(port);
        AuthInfo authInfo = createAuthInfo();
        HomekitRoot bridge = homekitServer.createBridge(authInfo, model, manufacturer, model, serialNumber, firmwareRevision, hardwareRevision);

        for (VirtualConsoleButton vcb : buttons) {
            logger.info("Registering accessory: " + vcb);
            bridge.addAccessory(vcb);
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


        logger.info("Started homekit service successfully with PIN " + authInfo.getPin());

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


    private String createRandomPin(){
        Random random = new Random();

        String number1 = String.valueOf(random.nextInt(999));
        String number2 = String.valueOf(random.nextInt(99));
        String number3 = String.valueOf(random.nextInt(999));

        number1 = StringUtils.leftPad(number1,3, "0");
        number2 = StringUtils.leftPad(number2,2, "0");
        number3 = StringUtils.leftPad(number3,3, "0");

        return number1+"-"+number2+"-"+number3;
    }




}
