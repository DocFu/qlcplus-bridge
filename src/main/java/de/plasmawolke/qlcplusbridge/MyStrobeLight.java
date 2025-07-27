package de.plasmawolke.qlcplusbridge;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.plasmawolke.qlcplusbridge.simdesk.DmxStateStore;
import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithBrightness;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

public class MyStrobeLight implements LightbulbAccessory, AccessoryWithBrightness {

    private int id;
    private String name;

    private List<MyLight> lights;

    private int lastDmxStrobeValue = 50;

    public MyStrobeLight(int id, String name, List<MyLight> lights) {
        this.id = id;
        this.name = name;
        this.lights = lights;
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
        // TODO: Implement identify
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("95-6");
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("AX5 Strobe");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture("Astera");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture("1.0");
    }

    @Override
    public CompletableFuture<Integer> getBrightness() {

        MyLight referenceLight = lights.get(0);

        Short dmxStrobeValue = DmxStateStore.getInstance().get(referenceLight.getDmxStrobeChannel());
        if (dmxStrobeValue == null) {
            return CompletableFuture.completedFuture(0);
        }
        int hapBrightness = (int) (dmxStrobeValue / 255.0 * 100);
        return CompletableFuture.completedFuture(hapBrightness);

    }

    @Override
    public CompletableFuture<Void> setBrightness(Integer value) throws Exception {

        for (MyLight light : lights) {
            int valueToSet = (int) (value * 255.0 / 100);

            if (valueToSet > 6) {
                lastDmxStrobeValue = valueToSet;
                DmxStateStore.getInstance().put(light.getDmxStrobeChannel(), (short) valueToSet);
            } else {
                DmxStateStore.getInstance().put(light.getDmxStrobeChannel(), (short) 0);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeBrightness(HomekitCharacteristicChangeCallback callback) {
        // TODO: Implement subscribeBrightness
    }

    @Override
    public void unsubscribeBrightness() {
        // TODO: Implement unsubscribeBrightness
    }

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        MyLight referenceLight = lights.get(0);
        Short dmxStrobeValue = DmxStateStore.getInstance().get(referenceLight.getDmxStrobeChannel());
        if (dmxStrobeValue == null) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(dmxStrobeValue > 6);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        for (MyLight light : lights) {
            if (powerState) {
                DmxStateStore.getInstance().put(light.getDmxStrobeChannel(), (short) lastDmxStrobeValue);
            } else {
                DmxStateStore.getInstance().put(light.getDmxStrobeChannel(), (short) 0);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        // TODO: Implement subscribeLightbulbPowerState
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        // TODO: Implement unsubscribeLightbulbPowerState
    }

}
