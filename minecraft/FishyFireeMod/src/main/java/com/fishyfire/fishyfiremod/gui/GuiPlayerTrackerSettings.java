package com.fishyfire.fishyfiremod.gui;

import com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import com.fishyfire.fishyfiremod.config.ConfigHandler;

public class GuiPlayerTrackerSettings extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();

    // buttons for click handling (we will draw color boxes manually)
    private GuiButton[] redButtons = new GuiButton[3];
    private GuiButton[] greenButtons = new GuiButton[3];
    private GuiButton[] blueButtons = new GuiButton[3];
    private GuiButton[] yellowButtons = new GuiButton[3];

    private GuiButton editHudButton;
    private GuiButton doneButton;
    private GuiButton backgroundToggleButton;
    private boolean backgroundEnabled;

    @Override
    public void initGui() {
        int centerX = width / 2;
        int startY = height / 2 - 80;

        // create invisible buttons for color choices; we'll draw colored rectangles over them
        int boxW = 20; int boxH = 20; int gap = 6;
        int baseX = centerX + 40; // right side

        // formatting codes for displayString (uses '§' formatting when rendered)
        char[] redCodes = {'c','d','5'};
        char[] greenCodes = {'a','2','8'};
        char[] blueCodes = {'9','1','3'};
        char[] yellowCodes = {'e','6','f'};

        for (int i = 0; i < 3; i++) {
            redButtons[i] = new GuiButton(10 + i, baseX + (boxW + gap) * i, startY, boxW, boxH, "");
            greenButtons[i] = new GuiButton(20 + i, baseX + (boxW + gap) * i, startY + 30, boxW, boxH, "");
            blueButtons[i] = new GuiButton(30 + i, baseX + (boxW + gap) * i, startY + 60, boxW, boxH, "");
            yellowButtons[i] = new GuiButton(40 + i, baseX + (boxW + gap) * i, startY + 90, boxW, boxH, "");
            // leave displayString empty; we'll draw a single letter on top using exact RGB from PlayerTrackerHUD
            redButtons[i].displayString = "";
            greenButtons[i].displayString = "";
            blueButtons[i].displayString = "";
            yellowButtons[i].displayString = "";
        }

        editHudButton = new GuiButton(50, width - width/5 - 50, height/2, 100, 20, "Edit HUD Position");
        doneButton = new GuiButton(51, centerX - 50, startY + 140, 100, 20, "Done");

        backgroundEnabled = PlayerTrackerHUD.isShowBackground();
        backgroundToggleButton = new GuiButton(52, centerX + 60, startY + 120, 70, 20, backgroundEnabled ? "ON" : "OFF");

        buttonList.clear();
        for (int i = 0; i < 3; i++) {
            buttonList.add(redButtons[i]);
            buttonList.add(greenButtons[i]);
            buttonList.add(blueButtons[i]);
            buttonList.add(yellowButtons[i]);
        }
        buttonList.add(editHudButton);
        buttonList.add(backgroundToggleButton);
        buttonList.add(doneButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int centerX = width / 2;
        int startY = height / 2 - 80;

        drawCenteredString(fontRendererObj, "FishyFiree Player Tracker Settings", centerX, startY - 30, 0xFFFFFF);

        drawString(fontRendererObj, "Red team color", centerX - 140, startY + 6, 0xCCCCCC);
        drawString(fontRendererObj, "Green team color", centerX - 140, startY + 36, 0xCCCCCC);
        drawString(fontRendererObj, "Blue team color", centerX - 140, startY + 66, 0xCCCCCC);
        drawString(fontRendererObj, "Yellow team color", centerX - 140, startY + 96, 0xCCCCCC);
        drawString(fontRendererObj, "Background", centerX - 140, startY + 120, 0xCCCCCC);

        // draw labels and standard buttons first
        super.drawScreen(mouseX, mouseY, partialTicks);

        // draw letters on top of buttons using exact HUD colors, and draw a thin selection border
        for (int i = 0; i < 3; i++) {
            int rc = PlayerTrackerHUD.getRedOptionColor(i);
            int gc = PlayerTrackerHUD.getGreenOptionColor(i);
            int bc = PlayerTrackerHUD.getBlueOptionColor(i);
            int yc = PlayerTrackerHUD.getYellowOptionColor(i);
            // center of each button
            int rx = redButtons[i].xPosition + redButtons[i].width/2 - fontRendererObj.getStringWidth("R")/2;
            int ry = redButtons[i].yPosition + (redButtons[i].height - fontRendererObj.FONT_HEIGHT)/2;
            int gx = greenButtons[i].xPosition + greenButtons[i].width/2 - fontRendererObj.getStringWidth("G")/2;
            int gy = greenButtons[i].yPosition + (greenButtons[i].height - fontRendererObj.FONT_HEIGHT)/2;
            int bx = blueButtons[i].xPosition + blueButtons[i].width/2 - fontRendererObj.getStringWidth("B")/2;
            int by = blueButtons[i].yPosition + (blueButtons[i].height - fontRendererObj.FONT_HEIGHT)/2;
            int yx = yellowButtons[i].xPosition + yellowButtons[i].width/2 - fontRendererObj.getStringWidth("Y")/2;
            int yy = yellowButtons[i].yPosition + (yellowButtons[i].height - fontRendererObj.FONT_HEIGHT)/2;
            drawString(fontRendererObj, "R", rx, ry, rc);
            drawString(fontRendererObj, "G", gx, gy, gc);
            drawString(fontRendererObj, "B", bx, by, bc);
            drawString(fontRendererObj, "Y", yx, yy, yc);
        }

        int sel;
        sel = PlayerTrackerHUD.getRedChoice();
        drawHollowBorder(redButtons[sel], 0xFFFFFFFF);
        sel = PlayerTrackerHUD.getGreenChoice();
        drawHollowBorder(greenButtons[sel], 0xFFFFFFFF);
        sel = PlayerTrackerHUD.getBlueChoice();
        drawHollowBorder(blueButtons[sel], 0xFFFFFFFF);
        sel = PlayerTrackerHUD.getYellowChoice();
        drawHollowBorder(yellowButtons[sel], 0xFFFFFFFF);
    }

    private void drawSelectionBorder(GuiButton b) {
        drawHollowBorder(b, 0xFFFFFFFF);
    }

    private int getContrastColor(int rgba) {
        int rgb = rgba & 0xFFFFFF;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        double lum = 0.2126*r + 0.7152*g + 0.0722*b;
        return lum < 128 ? 0xFFFFFF : 0x000000;
    }

    private void drawHollowBorder(GuiButton b, int color) {
        // top
        drawRect(b.xPosition - 1, b.yPosition - 1, b.xPosition + b.width + 1, b.yPosition, color);
        // bottom
        drawRect(b.xPosition - 1, b.yPosition + b.height, b.xPosition + b.width + 1, b.yPosition + b.height + 1, color);
        // left
        drawRect(b.xPosition - 1, b.yPosition, b.xPosition, b.yPosition + b.height, color);
        // right
        drawRect(b.xPosition + b.width, b.yPosition, b.xPosition + b.width + 1, b.yPosition + b.height, color);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int id = button.id;
        if (id >= 10 && id <= 12) {
            PlayerTrackerHUD.setRedChoice(id - 10);
        } else if (id >= 20 && id <= 22) {
            PlayerTrackerHUD.setGreenChoice(id - 20);
        } else if (id >= 30 && id <= 32) {
            PlayerTrackerHUD.setBlueChoice(id - 30);
        } else if (id >= 40 && id <= 42) {
            PlayerTrackerHUD.setYellowChoice(id - 40);
        } else if (id == 52) {
            backgroundEnabled = !backgroundEnabled;
            backgroundToggleButton.displayString = backgroundEnabled ? "ON" : "OFF";
            PlayerTrackerHUD.setShowBackground(backgroundEnabled);
        } else if (id == 50) {
            mc.displayGuiScreen(new com.fishyfire.fishyfiremod.gui.GuiEditPlayerTrackerPosition());
        } else if (id == 51) {
            // Done - save and close
            // persist
            ConfigHandler.saveConfig();
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §aSuccessfully updated player tracker settings!"));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            // close and persist
            ConfigHandler.saveConfig();
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §aSuccessfully updated player tracker settings!"));
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }
}

