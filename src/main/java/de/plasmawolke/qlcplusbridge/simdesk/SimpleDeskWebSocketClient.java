package de.plasmawolke.qlcplusbridge.simdesk;

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
public class SimpleDeskWebSocketClient implements VetoableChangeListener {

    private final static Logger logger = LoggerFactory.getLogger(SimpleDeskWebSocketClient.class);

    private WebSocketClient client = new WebSocketClient();
    private URI wsUri;
    private Session wsSession = null;

    public SimpleDeskWebSocketClient(String host, int port) throws URISyntaxException {
        this.wsUri = new URI("ws://" + host + ":" + port + "/qlcplusWS");
    }

    public void connect() throws Exception {

        if (client.isStopped()) {
            client.start();
        }

        if (wsSession == null || wsSession.isOpen()) {
            logger.info("Connecting to QLC+ WebSocket: " + wsUri);
            client.connect(this, wsUri, new ClientUpgradeRequest());
        }

    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("Connected to QLC+ WebSocket: " + session.getRemoteAddress());
        this.wsSession = session;

        sendHelloMessage();

    }

    @OnWebSocketMessage
    public void onMessage(String message) {

        // QLC+API|getChannelsValues|1|34|0.#000000|2|92|

        String[] parts = StringUtils.split(message, "|");

        int i = 2;
        while (i < parts.length - 1) {
            String kanalStr = parts[i];
            String wertStr = parts[i + 1];

            int kanal = Integer.parseInt(kanalStr);
            short wert = Short.parseShort(wertStr);

            System.out.println("Kanal: " + kanal + ", Wert: " + wert);

            // Prüfe, ob das nächste Feld existiert und mit "0.#" beginnt
            if (i + 2 < parts.length && parts[i + 2].startsWith("0.#")) {
                i += 3; // Optionales Feld überspringen
            } else {
                i += 2; // Kein optionales Feld, nur Kanal und Wert
            }

            DmxStateStore.getInstance().put(kanal, wert);
        }

    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {

    }

    public void sendDmxValue(int channel, short value) {
        String message = "CH|" + channel + "|" + value;

        // CH|11|36

        try {
            checkThenReconnectOrThrow(null);
        } catch (Exception e) {

        }

        if (wsSession == null) {
            logger.warn("WebSocket session is not established. Cannot send message [" + message + "]");
            return;
        }

        try {
            Future<Void> fut;
            fut = wsSession.getRemote().sendStringByFuture(message);
            fut.get(250, TimeUnit.MILLISECONDS); // wait for send to complete.

        } catch (Exception e) {

            logger.error("QLC+ WebSocket messages [" + message + "] could not be sent: ", e);
        }

    }

    public void sendHelloMessage() {

        try {
            checkThenReconnectOrThrow(null);
        } catch (Exception e) {

        }

        logger.debug("Sending QLC+ WebSocket Hello message");

        String message = "QLC+API|getChannelsValues|1|1|43";

        if (wsSession == null) {
            logger.warn("WebSocket session is not established. Cannot send hello message.");
            return;
        }

        try {
            Future<Void> fut;
            fut = wsSession.getRemote().sendStringByFuture(message);
            fut.get(250, TimeUnit.MILLISECONDS); // wait for send to complete.

        } catch (Exception e) {

            logger.error("QLC+ WebSocket messages [" + message + "] could not be sent: ", e);
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
                throw new PropertyVetoException(
                        "QLC+ WebSocket is not available. Is QLC+ started with remote web access?", evt);
            }

        }

    }

}
