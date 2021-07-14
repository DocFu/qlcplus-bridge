package de.plasmawolke.qlcplusbridge.hap;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthState implements Serializable {


    private final String pin;
    private final String mac;
    private final BigInteger salt;
    private final byte[] privateKey;
    private final String setupId;
    private final ConcurrentMap<String, byte[]> userKeyMap = new ConcurrentHashMap<>();

    public AuthState(String pin, String mac, BigInteger salt, byte[] privateKey, String setupId) {
        this.pin = pin;
        this.salt = salt;
        this.privateKey = privateKey;
        this.mac = mac;
        this.setupId = setupId;
    }

    public String getPin() {
        return pin;
    }

    public String getMac() {
        return mac;
    }

    public BigInteger getSalt() {
        return salt;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public String getSetupId() {
        return setupId;
    }

    public ConcurrentMap<String, byte[]> getUserKeyMap() {
        return userKeyMap;
    }
}
