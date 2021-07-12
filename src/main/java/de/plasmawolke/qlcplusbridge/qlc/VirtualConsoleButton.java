package de.plasmawolke.qlcplusbridge.qlc;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class VirtualConsoleButton implements LightbulbAccessory {


    private static final Logger logger = LoggerFactory.getLogger(VirtualConsoleButton.class);

    private int id;
    private String name;

    private String serialNumber = "1234-5678"; // TODO
    private String model = getClass().getSimpleName();
    private String manufacturer = "QLC+ Bridge";
    private String firmwareRevision = "0.1"; // TODO

    private boolean state;

    public VirtualConsoleButton(int id, String name) {
        if (id < 2) {
            throw new IllegalArgumentException("'id' must be greater than 1.");
        }
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("'name' may not be empty.");
        }
        this.id = id;
        this.name = name;
    }

    public VirtualConsoleButton(int id, String name, boolean initialState) {
        this(id,name);
        state = initialState;
    }


    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return CompletableFuture.completedFuture(state);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        state = powerState;
        logger.info(this.toString());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {

    }

    @Override
    public void unsubscribeLightbulbPowerState() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public void identify() {
        logger.info("I'm a VirtualConsoleButton with id '" + id + "' and name '" + name + "'. My current state is " + (state ? "ON" : "OFF") + ".");
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture(serialNumber);
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture(model);
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture(manufacturer);
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture(firmwareRevision);
    }


    @Override
    public String toString() {
        return "VirtualConsoleButton{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }
}
