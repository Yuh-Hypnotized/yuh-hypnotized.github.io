package com.fishyfire.fishyfiremod.gui;

import com.fishyfire.fishyfiremod.hud.CounterHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import com.fishyfire.fishyfiremod.gui.GuiEditHUDPosition;

import java.io.IOException;
import net.minecraft.util.ChatComponentText;
import com.fishyfire.fishyfiremod.config.ConfigHandler;

public class GuiCounterSettings extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();

    private GuiButton toggleSoundButton;
    private GuiButton toggleMessageButton;
    private GuiButton toggleBackgroundButton;
    private GuiButton doneButton;
    private GuiTextField orangeField;
    private GuiTextField greenField;

    private boolean soundEnabled;
    private boolean messageEnabled;
    private boolean backgroundEnabled;

    @Override
    public void initGui() {
        soundEnabled = CounterHUD.isPlaySound();

        int centerX = width / 2;
        int startY = height / 2 - 70;

        // Toggle buttons (left labels, right buttons)
        toggleSoundButton = new GuiButton(0, centerX + 60, startY, 70, 20, getSoundButtonText());

        messageEnabled = CounterHUD.isShowToggleMessage();
        toggleMessageButton = new GuiButton(1, centerX + 60, startY + 30, 70, 20, getMessageButtonText());

        backgroundEnabled = CounterHUD.isShowBackground();
        toggleBackgroundButton = new GuiButton(6, centerX + 60, startY + 60, 70, 20, getBackgroundButtonText());

        // Edit HUD Position button: place it vertically centered and toward the right (about 1/5 from right)
        int editBtnWidth = 100;
        int targetCenterX = width - width / 5; // center x for the button
        int editBtnX = targetCenterX - editBtnWidth / 2;
        int editBtnY = height / 2; // y-axis centered
        GuiButton editHudButton = new GuiButton(5, editBtnX, editBtnY, editBtnWidth, 20, "Edit HUD Position");

        // Color threshold title
        // Two text fields side-by-side, horizontally aligned
        int tfWidth = 50;
        int gap = 10;
        orangeField = new GuiTextField(2, fontRendererObj, centerX - gap/2 - tfWidth, startY + 100, tfWidth, 20);
        orangeField.setMaxStringLength(3);
        orangeField.setText(String.valueOf(CounterHUD.getThresholdOrange()));

        greenField = new GuiTextField(3, fontRendererObj, centerX + gap/2, startY + 100, tfWidth, 20);
        greenField.setMaxStringLength(3);
        greenField.setText(String.valueOf(CounterHUD.getThresholdGreen()));

        doneButton = new GuiButton(4, centerX - 50, startY + 160, 100, 20, "Done");

        buttonList.clear();
        buttonList.add(toggleSoundButton);
        buttonList.add(toggleMessageButton);
        buttonList.add(toggleBackgroundButton);
        buttonList.add(editHudButton);
        buttonList.add(doneButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Draw semi-transparent background
        drawDefaultBackground();

        int centerX = width / 2;
        int startY = height / 2 - 70;

        // Title
        drawCenteredString(fontRendererObj, "FishyFiree Counter Settings", centerX, startY - 30, 0xFFFFFF);

        // Labels
        drawString(fontRendererObj, "Toggle Sound", centerX - 100, startY + 6, 0xCCCCCC);
        drawString(fontRendererObj, "Toggle Message", centerX - 100, startY + 36, 0xCCCCCC);
        drawString(fontRendererObj, "Background", centerX - 100, startY + 66, 0xCCCCCC);
        drawString(fontRendererObj, "Color Threshold", centerX - 100, startY + 86, 0xCCCCCC);
        // Note for threshold
        drawString(fontRendererObj, "*Threshold 1 must be LESS than Threshold 2.", centerX - 100, startY + 130, 0x999999);

        // Text fields
        orangeField.drawTextBox();
        greenField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            soundEnabled = !soundEnabled;
            toggleSoundButton.displayString = getSoundButtonText();
        } else if (button.id == 1) {
            messageEnabled = !messageEnabled;
            toggleMessageButton.displayString = getMessageButtonText();
        } else if (button.id == 6) {
            backgroundEnabled = !backgroundEnabled;
            toggleBackgroundButton.displayString = getBackgroundButtonText();
            // apply immediately so user can see the change without re-opening
            CounterHUD.setShowBackground(backgroundEnabled);
        } else if (button.id == 4) {
            saveAndClose();
        } else if (button.id == 5) {
            // open HUD position editor
            mc.displayGuiScreen(new GuiEditHUDPosition());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            if (orangeField.isFocused() || greenField.isFocused()) {
                orangeField.setFocused(false);
                greenField.setFocused(false);
                return;
            }
            saveAndClose();
            return;
        }

        boolean digitOrCtrl = Character.isDigit(typedChar) || typedChar == '\b';
        if (digitOrCtrl) {
            if (orangeField.isFocused()) {
                orangeField.textboxKeyTyped(typedChar, keyCode);
            }
            if (greenField.isFocused()) {
                greenField.textboxKeyTyped(typedChar, keyCode);
            }
        } else if (keyCode == 15) { // TAB
            if (orangeField.isFocused()) {
                orangeField.setFocused(false);
                greenField.setFocused(true);
            } else if (greenField.isFocused()) {
                greenField.setFocused(false);
                orangeField.setFocused(true);
            } else {
                orangeField.setFocused(true);
            }
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        orangeField.mouseClicked(mouseX, mouseY, mouseButton);
        greenField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        orangeField.updateCursorCounter();
        greenField.updateCursorCounter();
    }

    private void saveAndClose() {
        String s1 = orangeField.getText();
        String s2 = greenField.getText();

        boolean valid = s1 != null && s2 != null && s1.matches("\\d+") && s2.matches("\\d+");
        if (!valid) {
            // close and notify invalid
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §cInvalid number! Setting failed!"));
            return;
        }

        int orange = Integer.parseInt(s1);
        int green = Integer.parseInt(s2);

        if (orange >= green) {
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §cInvalid number! Setting failed!"));
            return;
        }

        // valid — save settings
        CounterHUD.setPlaySound(soundEnabled);
        CounterHUD.setShowToggleMessage(messageEnabled);
        CounterHUD.setShowBackground(backgroundEnabled);
        CounterHUD.setThresholds(Math.max(1, orange), Math.max(2, green));

        // persist config
        ConfigHandler.saveConfig();

        mc.displayGuiScreen(null);
        if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §aSuccessfully updated counter settings!"));
    }

    private int parseThreshold(String text, int defaultValue) {
        if (text == null || text.isEmpty()) return defaultValue;
        try {
            int val = Integer.parseInt(text);
            return Math.max(1, Math.min(val, 999));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String getSoundButtonText() {
        return soundEnabled ? "ON" : "OFF";
    }

    private String getMessageButtonText() {
        return messageEnabled ? "ON" : "OFF";
    }

    private String getBackgroundButtonText() {
        return backgroundEnabled ? "ON" : "OFF";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
