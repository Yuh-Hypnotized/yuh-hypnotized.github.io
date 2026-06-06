package com.fishyfire.fishyfiremod.gui;

import com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class GuiEditPlayerTrackerPosition extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private GuiButton doneButton;

    private int hudX;
    private int hudY;

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    @Override
    public void initGui() {
        int centerX = width / 2;
        int startY = height / 2 - 10;
        doneButton = new GuiButton(0, centerX - 50, startY + 80, 100, 20, "Done");
        buttonList.clear();
        buttonList.add(doneButton);

        hudX = PlayerTrackerHUD.getHudPosX();
        hudY = PlayerTrackerHUD.getHudPosY();
        if (hudX < 0 || hudY < 0) {
            ScaledResolution res = new ScaledResolution(mc);
            hudX = res.getScaledWidth() / 4;
            hudY = res.getScaledHeight() / 4;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        ScaledResolution res = new ScaledResolution(mc);

        // draw preview of player tracker HUD
        PlayerTrackerHUD.drawAt(res, hudX, hudY);

        // compute approximate HUD bounds (reuse CounterHUD style: 160 width, lines height)
        int hudW = 160;
        int hudH = mc.fontRendererObj.FONT_HEIGHT * 4 + 6;

        int x1 = hudX - 4;
        int y1 = hudY - 4;
        int x2 = hudX + hudW + 4;
        int y2 = hudY + hudH + 4;

        int borderColor = 0x5500AAFF;
        drawRect(x1, y1, x2, y2, borderColor);
        int outline = 0xFF00CCFF;
        drawRect(x1, y1, x1 + 1, y2, outline);
        drawRect(x2 - 1, y1, x2, y2, outline);
        drawRect(x1, y1, x2, y1 + 1, outline);
        drawRect(x1, y2 - 1, x2, y2, outline);

        drawCenteredString(fontRendererObj, "Edit PlayerTracker Position - drag the box to move", width/2, 20, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int hudW = 160;
        int hudH = mc.fontRendererObj.FONT_HEIGHT * 4 + 6;
        int x1 = hudX - 4; int y1 = hudY - 4; int x2 = hudX + hudW + 4; int y2 = hudY + hudH + 4;
        if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2 && mouseButton == 0) {
            dragging = true;
            dragOffsetX = mouseX - hudX;
            dragOffsetY = mouseY - hudY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (dragging) {
            hudX = mouseX - dragOffsetX;
            hudY = mouseY - dragOffsetY;
            hudX = Math.max(0, Math.min(hudX, width - 20));
            hudY = Math.max(0, Math.min(hudY, height - 10));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            PlayerTrackerHUD.setHudPos(hudX, hudY);
            mc.displayGuiScreen(new GuiPlayerTrackerSettings());
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }
}

