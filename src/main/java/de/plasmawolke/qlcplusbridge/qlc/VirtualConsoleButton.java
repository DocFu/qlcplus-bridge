package de.plasmawolke.qlcplusbridge.qlc;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualConsoleButton implements LightbulbAccessory {


    public static final String CHANGE_PROPERTY_NAME = "VirtualConsoleButtonState";
    private static final Logger logger = LoggerFactory.getLogger(VirtualConsoleButton.class);
    private VetoableChangeSupport propertyChangeSupport = new VetoableChangeSupport(this);

    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    private int accessoryId;

    private int qlcId;
    private String name;

    private String serialNumber = "1234-5678"; // TODO
    private String model = getClass().getSimpleName();
    private String manufacturer = "QLC+ Bridge";
    private String firmwareRevision = "0.1"; // TODO

    private AtomicBoolean state = new AtomicBoolean(false);

    public VirtualConsoleButton(int qlcId, String name) {

        if (qlcId < 0) {
            throw new IllegalArgumentException("'qlcId' must be greater than 0.");
        }
        this.qlcId = qlcId;

        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("'name' may not be empty.");
        }
        this.name = name;

        createAndSetAccessoryId();

    }

    public VirtualConsoleButton(int qlcId, String name, boolean initialState) {
        this(qlcId, name);
        state = new AtomicBoolean(initialState);
    }

    public void addPropertyChangeListener(VetoableChangeListener pcl) {
        propertyChangeSupport.addVetoableChangeListener(pcl);
    }

    public void removePropertyChangeListener(VetoableChangeListener pcl) {
        propertyChangeSupport.removeVetoableChangeListener(pcl);
    }


    private void createAndSetAccessoryId() {
        accessoryId = (qlcId + name).hashCode();
        if (accessoryId < 0) {
            accessoryId = accessoryId * -1;
        }
    }


    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return CompletableFuture.completedFuture(state.get());
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean newState) throws Exception {


        if (state.compareAndSet(!newState, newState)) {
            if (subscribeCallback != null) {
                subscribeCallback.changed();
            }

            try{
                propertyChangeSupport.fireVetoableChange(CHANGE_PROPERTY_NAME, !newState, newState);
                logger.info("Homekit: '" + name + "' --> " + (newState ? "ON" : "OFF") + "");
            } catch (Exception e){
                logger.error(e.getMessage());
                throw new Exception(e.getMessage());
            }

        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Additional methode without firing a property change.
     *
     * @param newState
     * @return
     * @throws Exception
     */
    public CompletableFuture<Void> setLightbulbPowerStateFromSocket(boolean newState) throws Exception {
        if (state.compareAndSet(!newState, newState)) {
            if (subscribeCallback != null) {
                subscribeCallback.changed();
            }
            logger.info("QLC+: '" + name + "' --> " + (newState ? "ON" : "OFF") + "");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        this.subscribeCallback = callback;
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        this.subscribeCallback = null;
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
        logger.info("Identifying " + this.toString());
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

    public int getQlcId() {
        return qlcId;
    }

    @Override
    public String toString() {
        return "VirtualConsoleButton{" +
                "accessoryId=" + accessoryId +
                ", qlcId=" + qlcId +
                ", name='" + name + '\'' +
                ", state=" + (state.get() ? "ON" : "OFF") +
                '}';
    }
}
