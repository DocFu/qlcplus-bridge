package de.plasmawolke.qlcplusbridge.qlc;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class VirtualConsoleButton implements LightbulbAccessory {


    private static final Logger logger = LoggerFactory.getLogger(VirtualConsoleButton.class);

    private int accessoryId;

    private int htmlId;
    private String name;

    private String serialNumber = "1234-5678"; // TODO
    private String model = getClass().getSimpleName();
    private String manufacturer = "QLC+ Bridge";
    private String firmwareRevision = "0.1"; // TODO

    private boolean state;

    public VirtualConsoleButton(int htmlId, String name) {

        if (htmlId < 0) {
            throw new IllegalArgumentException("'htmlId' must be greater than 0.");
        }
        this.htmlId = htmlId;

        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("'name' may not be empty.");
        }
        this.name = name;

        createAndSetAccessoryId();

    }

    public VirtualConsoleButton(int htmlId, String name, boolean initialState) {
        this(htmlId,name);
        state = initialState;
    }

    private void createAndSetAccessoryId(){
        accessoryId = (htmlId + name).hashCode();
        if(accessoryId < 0){
            accessoryId = accessoryId * -1;
        }
    }


    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return CompletableFuture.completedFuture(state);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        state = powerState;
        logger.info("Changed power state for "+ this.toString()+".");
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
        return accessoryId;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public void identify() {
        logger.info("Identifying "+this.toString());
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
                "accessoryId=" + accessoryId +
                ", htmlId=" + htmlId +
                ", name='" + name + '\'' +
                ", state=" + (state?"on":"off") +
                '}';
    }
}
