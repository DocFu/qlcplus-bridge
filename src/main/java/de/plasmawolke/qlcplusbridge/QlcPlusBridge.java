package de.plasmawolke.qlcplusbridge;

import de.plasmawolke.qlcplusbridge.hap.HomekitService;
import de.plasmawolke.qlcplusbridge.qlc.NoButtonsFoundException;
import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleClient;
import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleButton;
import de.plasmawolke.qlcplusbridge.qlc.VirtualConsoleUnavailableException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class QlcPlusBridge {

    private static final Logger logger = LoggerFactory.getLogger(QlcPlusBridge.class);

    public static void main(String[] args)  {
        logger.info("Running QLC+ Bridge...");



        VirtualConsoleClient virtualConsoleClient = new VirtualConsoleClient("http://localhost:9999/");
        Collection<VirtualConsoleButton> buttons = null;
        try {
            buttons = virtualConsoleClient.collectButtons();
        } catch (NoButtonsFoundException e) {
           exitWithError("No buttons have been marked for export on QLC+ Virtual Console.  Mark some (by using blue button font color) and restart bridge.");
        } catch (VirtualConsoleUnavailableException e) {
            exitWithError("QLC+ Virtual Console seems not to be accessible on '"+e.getUrl()+"'. Is QLC+ started with web option: 'qlcplus -w'?");
        }

        HomekitService homekitService = new HomekitService();
        try {
            homekitService.runWithAccessories(buttons);
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " +e.getMessage());
        }


    }


    private static void exitWithError(String message){
        if(StringUtils.isBlank(message)){
            logger.error("Exiting with undefined error.");
        } else {
            logger.error(message);
        }

        logger.info("Exiting with error.");
        System.exit(1);
    }
}
