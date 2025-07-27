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

    // wird beim einschalten gesetzt
    private int lastDmxBrightness = 50; // nicht zu hell und nicht zu dunkel

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
        logger.info(
                "Identifying light: id={}, name='{}', brightnessChannel={}, colorTemperatureChannel={}, hueChannel={}, saturationChannel={}, strobeChannel={}",
                id, name, getDmxBrightnessChannel(), getDmxColorTemperatureChannel(), getDmxHueChannel(),
                getDmxSaturationChannel(), getDmxStrobeChannel());
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("DMX_PROFILE_95");
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("AX5");
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
        logger.info("Setting brightness for '{}' to {} (DMX value: {}, channel: {})", name, value,
                (short) (value * 255.0 / 100), getDmxBrightnessChannel());
        DmxStateStore.getInstance().put(getDmxBrightnessChannel(), (short) (value * 255.0 / 100));
        if (value > 0) {
            lastDmxBrightness = (short) (value * 255.0 / 100);
        }
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
        short dmxValue = powerState ? (short) lastDmxBrightness : (short) 0;
        logger.info("Setting lightbulb power state for '{}' to {} (DMX value: {}, channel: {})", name, powerState,
                dmxValue, getDmxBrightnessChannel());
        DmxStateStore.getInstance().put(getDmxBrightnessChannel(), dmxValue);
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
        int hapColorTemperature = (int) (400 - (dmxColorTemperature / 255.0) * (400 - 50));
        logger.info("Color temperature: {} -> {}", dmxColorTemperature, hapColorTemperature);
        return CompletableFuture.completedFuture(hapColorTemperature);
    }

    @Override
    public CompletableFuture<Void> setColorTemperature(Integer value) throws Exception {
        short dmxValue = (short) (((400 - value) * 255.0) / (400 - 50));
        logger.info("Setting color temperature for '{}' to {} (DMX value: {}, channel: {})", name, value, dmxValue,
                getDmxColorTemperatureChannel());
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
        short dmxValue = (short) (value * 255.0 / 360.0);
        logger.info("Setting hue for '{}' to {} (DMX value: {}, channel: {})", name, value, dmxValue,
                getDmxHueChannel());
        DmxStateStore.getInstance().put(getDmxHueChannel(), dmxValue);
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
        short dmxValue = (short) (value * 255.0 / 100.0);
        logger.info("Setting saturation for '{}' to {} (DMX value: {}, channel: {})", name, value, dmxValue,
                getDmxSaturationChannel());
        DmxStateStore.getInstance().put(getDmxSaturationChannel(), dmxValue);
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

    public int getDmxColorTemperatureChannel() {
        return dmxChannel + 1;
    }

    public int getDmxHueChannel() {
        return dmxChannel + 3;
    }

    public int getDmxSaturationChannel() {
        return dmxChannel + 4;
    }

    public int getDmxStrobeChannel() {
        return dmxChannel + 5;
    }

}
