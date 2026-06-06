package com.fishyfire.fishyfiremod.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CounterHUD {

    private static final int COLOR_YELLOW       = 0xFFFF55;
    private static final int COLOR_GREEN        = 0x55FF55;
    private static final int COLOR_LIGHT_YELLOW = 0xFFFF55;
    private static final int COLOR_ORANGE       = 0xFFAA00;
    private static final int COLOR_RED          = 0xFF5555;

    private static boolean enabled = true;
    private static boolean playSound = true;
    private static boolean showToggleMessage = true;
    private static boolean showBackground = false;
    private static int thresholdOrange = 20;
    private static int thresholdGreen = 64;
    // HUD position in scaled screen coordinates. If negative, compute defaults.
    private static int hudPosX = -1;
    private static int hudPosY = -1;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
        if (playSound) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                float pitch = enabled ? 1.0F : 0.5F;
                mc.thePlayer.playSound("random.orb", 0.5F, pitch);
            }
        }
    }

    public static boolean isPlaySound() {
        return playSound;
    }

    public static void setPlaySound(boolean value) {
        playSound = value;
    }

    public static boolean isShowToggleMessage() {
        return showToggleMessage;
    }

    public static void setShowToggleMessage(boolean value) {
        showToggleMessage = value;
    }

    public static boolean isShowBackground() { return showBackground; }
    public static void setShowBackground(boolean v) { showBackground = v; }

    public static int getThresholdOrange() {
        return thresholdOrange;
    }

    public static int getThresholdGreen() {
        return thresholdGreen;
    }

    public static void setThresholds(int orange, int green) {
        thresholdOrange = orange;
        thresholdGreen = green;
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && enabled) {
            int x;
            int y;
            if (hudPosX >= 0 && hudPosY >= 0) {
                x = hudPosX;
                y = hudPosY;
            } else {
                x = event.resolution.getScaledWidth() * 3 / 4 - 20;
                y = event.resolution.getScaledHeight() / 4;
            }
            drawAt(event.resolution, x, y);
        }
    }
    /**
     * Draw the HUD at given scaled coordinates.
     */
    public static void drawAt(ScaledResolution resolution, int x, int y) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        // Use temporary CounterHUD instance to access non-static helpers
        CounterHUD tmp = new CounterHUD();

        int blockCount = tmp.countBlocksInInventory();
        int arrowCount = tmp.countArrowsInInventory();

        int blockNumColor = tmp.getCountColor(blockCount);
        int arrowNumColor = tmp.getCountColor(arrowCount);

        // optional background
        int lineH = fr.FONT_HEIGHT + 2;
        if (showBackground) {
            int bgW = Math.max(fr.getStringWidth("Blocks: " + blockCount), fr.getStringWidth("Arrows: " + arrowCount)) + 8;
            Gui.drawRect(x - 4, y - 4, x + bgW, y + lineH*2 + 6, 0x66000000);
        }

        fr.drawStringWithShadow("Blocks: ", x, y, COLOR_YELLOW);
        int blocksLabelWidth = fr.getStringWidth("Blocks: ");
        fr.drawStringWithShadow(String.valueOf(blockCount), x + blocksLabelWidth, y, blockNumColor);

        fr.drawStringWithShadow("Arrows: ", x, y + 10, COLOR_YELLOW);
        int arrowsLabelWidth = fr.getStringWidth("Arrows: ");
        fr.drawStringWithShadow(String.valueOf(arrowCount), x + arrowsLabelWidth, y + 10, arrowNumColor);
    }

    public static int getHudPosX() {
        return hudPosX;
    }

    public static int getHudPosY() {
        return hudPosY;
    }

    public static void setHudPos(int x, int y) {
        hudPosX = x;
        hudPosY = y;
    }

    private int getCountColor(int count) {
        if (count >= thresholdGreen) {
            return COLOR_GREEN;
        } else if (count >= thresholdOrange) {
            return COLOR_LIGHT_YELLOW;
        } else if (count > 0) {
            return COLOR_ORANGE;
        } else {
            return COLOR_RED;
        }
    }

    private int countBlocksInInventory() {
        int count = 0;
        InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            ItemStack stack = inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    private int countArrowsInInventory() {
        int count = 0;
        InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            ItemStack stack = inventory.mainInventory[i];
            if (stack != null && stack.getItem() == Items.arrow) {
                count += stack.stackSize;
            }
        }
        return count;
    }

}
