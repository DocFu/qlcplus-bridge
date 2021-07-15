package de.plasmawolke.qlcplusbridge.qlc;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class VirtualConsoleWebSocketClient implements VetoableChangeListener {

    private final static Logger logger = LoggerFactory.getLogger(VirtualConsoleWebSocketClient.class);
    private List<VirtualConsoleButton> buttons;


    private WebSocketClient client = new WebSocketClient();
    private URI wsUri;
    private Session wsSession = null;

    public VirtualConsoleWebSocketClient(String host, int port, List<VirtualConsoleButton> buttons) throws URISyntaxException {
        this.buttons = buttons;
        this.wsUri = new URI("ws://" + host + ":" + port + "/qlcplusWS");


        for (VirtualConsoleButton button : buttons) {
            button.addPropertyChangeListener(this);
        }


    }

    public void connect() throws Exception {

        if (client.isStopped()) {
            client.start();
        }

        if (wsSession == null || wsSession.isOpen()) {
            client.connect(this, wsUri, new ClientUpgradeRequest());
        }

    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("Connected to QLC+ WebSocket: " + session.getRemoteAddress());
        this.wsSession = session;
    }

    @OnWebSocketMessage
    public void onMessage(String message) {


        String[] parts = StringUtils.split(message, "|");

        int id = Integer.parseInt(parts[0]); // e.g. 31
        String name = parts[1]; // e.g. BUTTON
        int value = Integer.parseInt(parts[2]); // e.g. 127

        Optional<VirtualConsoleButton> optionalButton = findButtonByHtmlId(id);

        if (optionalButton.isEmpty()) {
            logger.debug("Discarding QLC+ WebSocket message because no button could be determined by message: " + message);
            return;
        }

        VirtualConsoleButton button = optionalButton.get();

        logger.debug("Receiving QLC+ WebSocket message for " + button);

        try {
            if (value == 0) {
                button.setLightbulbPowerStateFromSocket(false);
            } else if (value == 255) {
                button.setLightbulbPowerStateFromSocket(true);
            }
        } catch (Exception e) {
            logger.error("Could not set power state for " + button + ".", e);
        }


    }

    private Optional<VirtualConsoleButton> findButtonByHtmlId(int id) {
        for (VirtualConsoleButton button : buttons) {
            if (button.getQlcId() == id) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }


    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {


        if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
            VirtualConsoleButton button = (VirtualConsoleButton) evt.getSource();
            logger.debug("Sending QLC+ WebSocket message for " + button);

            int qlcId = button.getQlcId();


            checkThenReconnectOrThrow(evt);


            String message1 = qlcId + "|0";
            String message2 = qlcId + "|255";

            try {
                Future<Void> fut;
                fut = wsSession.getRemote().sendStringByFuture(message1);
                fut.get(250, TimeUnit.MILLISECONDS); // wait for send to complete.

                fut = wsSession.getRemote().sendStringByFuture(message2);
                fut.get(250, TimeUnit.MILLISECONDS); // wait for send to complete.

            } catch (Exception e) {
                logger.error("Error on button [" + qlcId + "] click...");
                logger.error("QLC+ WebSocket messages [" + message1 + "," + message2 + "] could not be sent: ", e);
            }
        }

    }

    private void checkThenReconnectOrThrow(PropertyChangeEvent evt) throws PropertyVetoException {

        try {
            wsSession.getRemote();
        } catch (WebSocketException wse) {
            logger.warn("Connection seems broken. Trying to reconnect...");
            try {
                connect();
                wsSession.getRemote();
                // XXX Updating state for HomeKit after QLC+ restart could be useful here.
            } catch (Exception e) {
                logger.error("Reconnect failed: " + e.getMessage());
                throw new PropertyVetoException("QLC+ WebSocket is not available. Is QLC+ started with remote web access?", evt);
            }

        }


    }


}
