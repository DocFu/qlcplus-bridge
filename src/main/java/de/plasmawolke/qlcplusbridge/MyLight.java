package de.plasmawolke.qlcplusbridge;

import java.util.concurrent.CompletableFuture;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithBrightness;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

public class MyLight implements LightbulbAccessory, AccessoryWithBrightness {

    @Override
    public int getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    @Override
    public CompletableFuture<String> getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public void identify() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'identify'");
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSerialNumber'");
    }

    @Override
    public CompletableFuture<String> getModel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModel'");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getManufacturer'");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFirmwareRevision'");
    }

    @Override
    public CompletableFuture<Integer> getBrightness() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBrightness'");
    }

    @Override
    public CompletableFuture<Void> setBrightness(Integer value) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBrightness'");
    }

    @Override
    public void subscribeBrightness(HomekitCharacteristicChangeCallback callback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subscribeBrightness'");
    }

    @Override
    public void unsubscribeBrightness() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribeBrightness'");
    }

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLightbulbPowerState'");
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLightbulbPowerState'");
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subscribeLightbulbPowerState'");
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribeLightbulbPowerState'");
    }

}
