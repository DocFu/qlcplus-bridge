package de.plasmawolke.qlcplusbridge;

import java.util.concurrent.CompletableFuture;

import de.plasmawolke.qlcplusbridge.simdesk.DmxStateStore;
import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithBrightness;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithColor;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithColorTemperature;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLight
        implements LightbulbAccessory, AccessoryWithBrightness, AccessoryWithColor, AccessoryWithColorTemperature {

    private static final Logger logger = LoggerFactory.getLogger(MyLight.class);

    private int id;
    private String name;
    private int dmxChannel;

    public MyLight(int id, String name, int dmxChannel) {
        this.id = id;
        this.name = name;
        this.dmxChannel = dmxChannel;
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
        logger.info("Identifying " + name);
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("1234567890");
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("MyLight");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture("MyManufacturer");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture("1.0");
    }

    @Override
    public CompletableFuture<Integer> getBrightness() {
        Short dmxBrightness = DmxStateStore.getInstance().get(getDmxBrightnessChannel());
        if (dmxBrightness == null) {
            return CompletableFuture.completedFuture(0);
        }
        int hapBrightness = (int) (dmxBrightness / 255.0 * 100);
        logger.info("Brightness: {} -> {}", hapBrightness, dmxBrightness);
        return CompletableFuture.completedFuture(hapBrightness);
    }

    @Override
    public CompletableFuture<Void> setBrightness(Integer value) throws Exception {
        logger.info("Setting brightness to " + value);
        DmxStateStore.getInstance().put(getDmxBrightnessChannel(), (short) (value * 255.0 / 100));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeBrightness(HomekitCharacteristicChangeCallback callback) {
        logger.info("Subscribing to brightness");
    }

    @Override
    public void unsubscribeBrightness() {
        logger.info("Unsubscribing from brightness");
    }

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        logger.info("Getting lightbulb power state");
        Short dmxBrightness = DmxStateStore.getInstance().get(getDmxBrightnessChannel());
        if (dmxBrightness == null) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(dmxBrightness > 0);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        logger.info("Setting lightbulb power state to " + powerState);
        DmxStateStore.getInstance().put(getDmxBrightnessChannel(), powerState ? (short) 255 : (short) 0);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        logger.info("Subscribing to lightbulb power state");
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        logger.info("Unsubscribing from lightbulb power state");
    }

    /*
     * Color Temperature
     */

    @Override
    public CompletableFuture<Integer> getColorTemperature() {
        Short dmxColorTemperature = DmxStateStore.getInstance().get(getDmxColorTemperatureChannel());
        if (dmxColorTemperature == null) {
            return CompletableFuture.completedFuture(140);
        }
        int hapColorTemperature = (int) (50 + (dmxColorTemperature / 255.0) * (400 - 50));
        logger.info("Color temperature: {} -> {}", dmxColorTemperature, hapColorTemperature);
        return CompletableFuture.completedFuture(hapColorTemperature);
    }

    @Override
    public CompletableFuture<Void> setColorTemperature(Integer value) throws Exception {
        logger.info("Setting color temperature to " + value);
        short dmxValue = (short) ((value - 50) * 255.0 / (400 - 50));
        DmxStateStore.getInstance().put(getDmxColorTemperatureChannel(), dmxValue);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeColorTemperature(HomekitCharacteristicChangeCallback callback) {
        logger.info("Subscribing to color temperature");
    }

    @Override
    public void unsubscribeColorTemperature() {
        logger.info("Unsubscribing from color temperature");
    }

    /*
     * Color
     */

    @Override
    public CompletableFuture<Double> getHue() {
        Short dmxHue = DmxStateStore.getInstance().get(getDmxHueChannel());
        if (dmxHue == null) {
            return CompletableFuture.completedFuture(0.0);
        }
        return CompletableFuture.completedFuture(dmxHue.doubleValue() / 255.0 * 360.0);
    }

    @Override
    public CompletableFuture<Void> setHue(Double value) throws Exception {
        logger.info("Setting hue to " + value);
        DmxStateStore.getInstance().put(getDmxHueChannel(), (short) (value * 255.0 / 360.0));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeHue(HomekitCharacteristicChangeCallback callback) {
        logger.info("Subscribing to hue");
    }

    @Override
    public void unsubscribeHue() {
        logger.info("Unsubscribing from hue");
    }

    @Override
    public CompletableFuture<Double> getSaturation() {
        Short dmxSaturation = DmxStateStore.getInstance().get(getDmxSaturationChannel());
        if (dmxSaturation == null) {
            return CompletableFuture.completedFuture(0.0);
        }
        return CompletableFuture.completedFuture(dmxSaturation.doubleValue() / 255.0 * 100.0);
    }

    @Override
    public CompletableFuture<Void> setSaturation(Double value) throws Exception {
        logger.info("Setting saturation to " + value);
        DmxStateStore.getInstance().put(getDmxSaturationChannel(), (short) (value * 255.0 / 100.0));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeSaturation(HomekitCharacteristicChangeCallback callback) {
        logger.info("Subscribing to saturation");
    }

    @Override
    public void unsubscribeSaturation() {
        logger.info("Unsubscribing from saturation");
    }

    //

    public int getDmxBrightnessChannel() {
        return dmxChannel;
    }

    public int getDmxHueChannel() {
        return dmxChannel + 3;
    }

    public int getDmxSaturationChannel() {
        return dmxChannel + 4;
    }

    public int getDmxColorTemperatureChannel() {
        return dmxChannel + 1;
    }

}
