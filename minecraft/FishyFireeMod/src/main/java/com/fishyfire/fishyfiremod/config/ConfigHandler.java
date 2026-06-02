package com.fishyfire.fishyfiremod.config;

import com.fishyfire.fishyfiremod.hud.CounterHUD;
import com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

    private static Configuration config;

    public static void loadConfig(File suggestedFile) {
        if (suggestedFile == null) return;
        config = new Configuration(suggestedFile);
        syncFromConfig();
    }

    private static void syncFromConfig() {
        if (config == null) return;
        config.load();

        // Counter
        boolean playSound = config.getBoolean("playSound", "counter", CounterHUD.isPlaySound(), "Play toggle sound");
        boolean showMsg = config.getBoolean("showToggleMessage", "counter", CounterHUD.isShowToggleMessage(), "Show toggle message in chat");
        int thO = config.getInt("thresholdOrange", "counter", CounterHUD.getThresholdOrange(), 1, 999, "Orange threshold");
        int thG = config.getInt("thresholdGreen", "counter", CounterHUD.getThresholdGreen(), 1, 999, "Green threshold");
        int cHudX = config.getInt("hudPosX", "counter", CounterHUD.getHudPosX(), -9999, 9999, "Counter HUD X (scaled)");
        int cHudY = config.getInt("hudPosY", "counter", CounterHUD.getHudPosY(), -9999, 9999, "Counter HUD Y (scaled)");
        boolean cBg = config.getBoolean("showBackground", "counter", CounterHUD.isShowBackground(), "Show background behind counter HUD");

        CounterHUD.setPlaySound(playSound);
        CounterHUD.setShowToggleMessage(showMsg);
        CounterHUD.setThresholds(thO, thG);
        CounterHUD.setHudPos(cHudX, cHudY);
        CounterHUD.setShowBackground(cBg);

        // PlayerTracker
        int r = config.getInt("redChoice", "playertracker", PlayerTrackerHUD.getRedChoice(), 0, 2, "Red choice index");
        int g = config.getInt("greenChoice", "playertracker", PlayerTrackerHUD.getGreenChoice(), 0, 2, "Green choice index");
        int b = config.getInt("blueChoice", "playertracker", PlayerTrackerHUD.getBlueChoice(), 0, 2, "Blue choice index");
        int y = config.getInt("yellowChoice", "playertracker", PlayerTrackerHUD.getYellowChoice(), 0, 2, "Yellow choice index");
        int pHudX = config.getInt("hudPosX", "playertracker", PlayerTrackerHUD.getHudPosX(), -9999, 9999, "PlayerTracker HUD X (scaled)");
        int pHudY = config.getInt("hudPosY", "playertracker", PlayerTrackerHUD.getHudPosY(), -9999, 9999, "PlayerTracker HUD Y (scaled)");
        boolean pBg = config.getBoolean("showBackground", "playertracker", PlayerTrackerHUD.isShowBackground(), "Show background behind player tracker HUD");

        PlayerTrackerHUD.setRedChoice(r);
        PlayerTrackerHUD.setGreenChoice(g);
        PlayerTrackerHUD.setBlueChoice(b);
        PlayerTrackerHUD.setYellowChoice(y);
        PlayerTrackerHUD.setHudPos(pHudX, pHudY);
        PlayerTrackerHUD.setShowBackground(pBg);

        if (config.hasChanged()) config.save();
    }

    public static void saveConfig() {
        if (config == null) return;

        // Counter
        config.getCategory("counter").get("playSound").set(CounterHUD.isPlaySound());
        config.getCategory("counter").get("showToggleMessage").set(CounterHUD.isShowToggleMessage());
        config.getCategory("counter").get("thresholdOrange").set(CounterHUD.getThresholdOrange());
        config.getCategory("counter").get("thresholdGreen").set(CounterHUD.getThresholdGreen());
        config.getCategory("counter").get("hudPosX").set(CounterHUD.getHudPosX());
        config.getCategory("counter").get("hudPosY").set(CounterHUD.getHudPosY());
        config.getCategory("counter").get("showBackground").set(CounterHUD.isShowBackground());

        // PlayerTracker
        config.getCategory("playertracker").get("redChoice").set(PlayerTrackerHUD.getRedChoice());
        config.getCategory("playertracker").get("greenChoice").set(PlayerTrackerHUD.getGreenChoice());
        config.getCategory("playertracker").get("blueChoice").set(PlayerTrackerHUD.getBlueChoice());
        config.getCategory("playertracker").get("yellowChoice").set(PlayerTrackerHUD.getYellowChoice());
        config.getCategory("playertracker").get("hudPosX").set(PlayerTrackerHUD.getHudPosX());
        config.getCategory("playertracker").get("hudPosY").set(PlayerTrackerHUD.getHudPosY());
        config.getCategory("playertracker").get("showBackground").set(PlayerTrackerHUD.isShowBackground());

        config.save();
    }
}

