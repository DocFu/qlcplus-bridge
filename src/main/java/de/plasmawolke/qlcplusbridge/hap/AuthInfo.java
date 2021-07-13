package de.plasmawolke.qlcplusbridge.hap;

import io.github.hapjava.server.HomekitAuthInfo;
import io.github.hapjava.server.impl.HomekitServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.util.UUID;
import java.util.function.Consumer;

public class AuthInfo implements HomekitAuthInfo {

    private static final Logger logger = LoggerFactory.getLogger(AuthInfo.class);

    private final AuthState authState;

    private Consumer<AuthState> callback;

    public AuthInfo(String pin) throws InvalidAlgorithmParameterException {
        this(new AuthState(pin, HomekitServer.generateMac(), HomekitServer.generateSalt(),
                HomekitServer.generateKey(), UUID.randomUUID().toString()));
    }

    public AuthInfo(AuthState authState) {
        this.authState = authState;
    }

    @Override
    public String getPin() {
        return authState.getPin();
    }

    @Override
    public String getMac() {
        return authState.getMac();
    }

    @Override
    public BigInteger getSalt() {
        return authState.getSalt();
    }

    @Override
    public byte[] getPrivateKey() {
        return authState.getPrivateKey();
    }


    @Override
    public void createUser(String username, byte[] publicKey) {
        if (!authState.getUserKeyMap().containsKey(username)) {
            authState.getUserKeyMap().putIfAbsent(username, publicKey);
            logger.info("Added pairing for " + username);
            notifyChange();
        } else {
            logger.debug("Already have a user for " + username);
        }
    }

    @Override
    public void removeUser(String username) {
        authState.getUserKeyMap().remove(username);
        logger.info("Removed pairing for " + username);
        notifyChange();
    }

    @Override
    public byte[] getUserPublicKey(String username) {
        return authState.getUserKeyMap().get(username);
    }

    public void onChange(Consumer<AuthState> callback) {
        this.callback = callback;
        notifyChange();
    }

    private void notifyChange() {
        if (callback != null) {
            callback.accept(authState);
        }
    }

    @Override
    public boolean hasUser() {
        return !authState.getUserKeyMap().isEmpty();
    }

}
