package de.plasmawolke.qlcplusbridge;

import de.plasmawolke.qlcplusbridge.hap.HomekitService;
import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleClient;
import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class QlcPlusBridge {

    private static final Logger logger = LoggerFactory.getLogger(QlcPlusBridge.class);

    public static void main(String[] args) throws Exception {
        logger.info("Running QLC+ Bridge...");



        VirtualConsoleClient virtualConsoleClient = new VirtualConsoleClient("http://localhost:9999/");
        Collection<VirtualConsoleButton> buttons = virtualConsoleClient.collectButtons();

        HomekitService homekitService = new HomekitService();
        homekitService.runWithAccessories(buttons);


    }
}
