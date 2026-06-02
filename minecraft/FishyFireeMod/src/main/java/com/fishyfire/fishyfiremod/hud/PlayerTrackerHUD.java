package com.fishyfire.fishyfiremod.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.Gui;

import java.util.List;

/**
 * PlayerTracker HUD - shows nearest player of each team (red/green/blue/yellow)
 */
public class PlayerTrackerHUD {

    private static boolean enabled = true;

    // color selection indices per team (0..2)
    private static int redChoice = 0;
    private static int greenChoice = 0;
    private static int blueChoice = 0;
    private static int yellowChoice = 0;

    // color code options (formatting char without §)
    private static final char[] RED_OPTS = {'c','d','5'}; // §c §d §5
    private static final char[] GREEN_OPTS = {'a','2','8'}; // §a §2 §8
    private static final char[] BLUE_OPTS = {'9','1','3'}; // §9 §1 §3
    private static final char[] YELLOW_OPTS = {'e','6','f'}; // §e §6 §f

    // HUD position
    private static int hudPosX = -1;
    private static int hudPosY = -1;
    private static volatile boolean openSettingsRequested = false;
    private static boolean showBackground = false;

    public static boolean isEnabled() { return enabled; }
    public static void toggle() { enabled = !enabled; }

    public static int getHudPosX() { return hudPosX; }
    public static int getHudPosY() { return hudPosY; }
    public static void setHudPos(int x, int y) { hudPosX = x; hudPosY = y; }

    public static void requestOpenSettings() { openSettingsRequested = true; }
    public static boolean consumeOpenSettings() { if (openSettingsRequested) { openSettingsRequested = false; return true; } return false; }

    public static boolean isShowBackground() { return showBackground; }
    public static void setShowBackground(boolean v) { showBackground = v; }

    public static int getRedChoice() { return redChoice; }
    public static int getGreenChoice() { return greenChoice; }
    public static int getBlueChoice() { return blueChoice; }
    public static int getYellowChoice() { return yellowChoice; }

    public static void setRedChoice(int v) { redChoice = Math.max(0, Math.min(2, v)); }
    public static void setGreenChoice(int v) { greenChoice = Math.max(0, Math.min(2, v)); }
    public static void setBlueChoice(int v) { blueChoice = Math.max(0, Math.min(2, v)); }
    public static void setYellowChoice(int v) { yellowChoice = Math.max(0, Math.min(2, v)); }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && enabled) {
            int x;
            int y;
            if (hudPosX >= 0 && hudPosY >= 0) {
                x = hudPosX;
                y = hudPosY;
            } else {
                x = event.resolution.getScaledWidth() / 4;
                y = event.resolution.getScaledHeight() / 4;
            }
            drawAt(event.resolution, x, y);
        }
    }

    public static void drawAt(ScaledResolution res, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        FontRenderer fr = mc.fontRendererObj;

        // find nearest player for each team
        EntityPlayer nearestRed = null;
        EntityPlayer nearestGreen = null;
        EntityPlayer nearestBlue = null;
        EntityPlayer nearestYellow = null;

        double dRed = Double.MAX_VALUE;
        double dGreen = Double.MAX_VALUE;
        double dBlue = Double.MAX_VALUE;
        double dYellow = Double.MAX_VALUE;

        List players = mc.theWorld.playerEntities;
        for (Object o : players) {
            if (!(o instanceof EntityPlayer)) continue;
            EntityPlayer p = (EntityPlayer) o;
            if (p == mc.thePlayer) continue;

            String formatted = p.getDisplayName().getFormattedText();
            // prefer to locate the actual username inside the formatted display name and use its color
            char lastColor = extractColorForUsername(formatted, p.getName());
            if (lastColor == '\0') {
                // if username not found, use the color of the first visible character
                lastColor = getFirstVisibleCharColor(formatted);
            }
            // compute squared distance
            double dx = p.posX - mc.thePlayer.posX;
            double dy = p.posY - mc.thePlayer.posY;
            double dz = p.posZ - mc.thePlayer.posZ;
            double distSq = dx*dx + dy*dy + dz*dz;

            if (isColorMatch(lastColor, RED_OPTS, redChoice)) {
                if (distSq < dRed) { dRed = distSq; nearestRed = p; }
            } else if (isColorMatch(lastColor, GREEN_OPTS, greenChoice)) {
                if (distSq < dGreen) { dGreen = distSq; nearestGreen = p; }
            } else if (isColorMatch(lastColor, BLUE_OPTS, blueChoice)) {
                if (distSq < dBlue) { dBlue = distSq; nearestBlue = p; }
            } else if (isColorMatch(lastColor, YELLOW_OPTS, yellowChoice)) {
                if (distSq < dYellow) { dYellow = distSq; nearestYellow = p; }
            }
        }

        int lineH = fr.FONT_HEIGHT + 2;
        int drawY = y;

        // draw a small header background for readability (optional)
        if (showBackground) {
            int bgW = 160;
            Gui.drawRect(x - 4, y - 4, x + bgW, y + lineH*4 + 6, 0x66000000);
        }

        if (nearestRed != null) {
            int col = mapChoiceToRenderColor(RED_OPTS, redChoice);
            drawPlayerLine(fr, nearestRed, dRed, x, drawY, col);
        } else {
            String label = "<Red>";
            int col = mapChoiceToRenderColor(RED_OPTS, redChoice);
            fr.drawStringWithShadow(label, x, drawY, col);
            int w = fr.getStringWidth(label);
            // draw ' - None' in light gray and italic using formatting codes (§7 gray, §o italic)
            fr.drawStringWithShadow(" §7§o- None", x + w, drawY, 0xFFFFFF);
        }
        drawY += lineH;

        if (nearestGreen != null) {
            int col = mapChoiceToRenderColor(GREEN_OPTS, greenChoice);
            drawPlayerLine(fr, nearestGreen, dGreen, x, drawY, col);
        } else {
            String label = "<Green>";
            int col = mapChoiceToRenderColor(GREEN_OPTS, greenChoice);
            fr.drawStringWithShadow(label, x, drawY, col);
            int w = fr.getStringWidth(label);
            fr.drawStringWithShadow(" §7§o- None", x + w, drawY, 0xFFFFFF);
        }
        drawY += lineH;

        if (nearestBlue != null) {
            int col = mapChoiceToRenderColor(BLUE_OPTS, blueChoice);
            drawPlayerLine(fr, nearestBlue, dBlue, x, drawY, col);
        } else {
            String label = "<Blue>";
            int col = mapChoiceToRenderColor(BLUE_OPTS, blueChoice);
            fr.drawStringWithShadow(label, x, drawY, col);
            int w = fr.getStringWidth(label);
            fr.drawStringWithShadow(" §7§o- None", x + w, drawY, 0xFFFFFF);
        }
        drawY += lineH;

        if (nearestYellow != null) {
            int col = mapChoiceToRenderColor(YELLOW_OPTS, yellowChoice);
            drawPlayerLine(fr, nearestYellow, dYellow, x, drawY, col);
        } else {
            String label = "<Yellow>";
            int col = mapChoiceToRenderColor(YELLOW_OPTS, yellowChoice);
            fr.drawStringWithShadow(label, x, drawY, col);
            int w = fr.getStringWidth(label);
            fr.drawStringWithShadow(" §7§o- None", x + w, drawY, 0xFFFFFF);
        }
    }

    private static void drawPlayerLine(FontRenderer fr, EntityPlayer p, double distSq, int x, int y, int color) {
        String name = p.getDisplayName().getUnformattedText();
        double dist = Math.sqrt(distSq);
        double yDiff = p.posY - Minecraft.getMinecraft().thePlayer.posY;
        String distStr = String.format("%.1f", dist) + "m";
        boolean positive = yDiff >= 0.0;
        String sign = positive ? "+" : "-";
        String mag = String.format("%.1f", Math.abs(yDiff)) + "m";
        // draw name in team color
        fr.drawStringWithShadow(name, x, y, color);
        int nameW = fr.getStringWidth(name);

        // draw distance (white), separator, then vertical diff with colored sign and gray magnitude
        int curX = x + nameW;
        String distOut = " " + distStr;
        fr.drawStringWithShadow(distOut, curX, y, 0xFFFFFF);
        curX += fr.getStringWidth(distOut);

        String sep = " | ";
        fr.drawStringWithShadow(sep, curX, y, 0xBBBBBB);
        curX += fr.getStringWidth(sep);

        // sign color: §b light aqua (0x55FFFF) for '+', §5 dark purple (0xAA55FF) for '-'
        int signColor = positive ? 0x55FFFF : 0xAA55FF;
        fr.drawStringWithShadow(sign, curX, y, signColor);
        curX += fr.getStringWidth(sign);

        // magnitude in light gray §7 -> 0xAAAAAA
        fr.drawStringWithShadow(mag, curX, y, 0xAAAAAA);
    }

    // (Removed older helper heuristics to simplify code — username-based color extraction is used instead.)

    // Attempt to find the real username inside the formatted display string and return the color
    // that applies to the first character of the username (or the most recent non-empty color before it).
    private static char extractColorForUsername(String formatted, String username) {
        if (formatted == null || username == null || username.isEmpty()) return '\0';
        java.util.List<Character> visChars = new java.util.ArrayList<>();
        java.util.List<Character> visColors = new java.util.ArrayList<>();
        char current = '\0';
        for (int i = 0; i < formatted.length(); i++) {
            char c = formatted.charAt(i);
            if (c == '§') {
                if (i + 1 < formatted.length()) {
                    current = Character.toLowerCase(formatted.charAt(i+1));
                    i++;
                    continue;
                }
            }
            visChars.add(c);
            visColors.add(current);
        }

        if (visChars.isEmpty()) return '\0';

        // convert username to char array for matching
        char[] uname = username.toCharArray();
        int n = visChars.size();
        for (int start = 0; start + uname.length <= n; start++) {
            boolean match = true;
            for (int k = 0; k < uname.length; k++) {
                if (visChars.get(start + k) != uname[k]) { match = false; break; }
            }
            if (match) {
                // found the username starting at index 'start'
                char col = visColors.get(start);
                if (col != '\0') return col;
                // otherwise find most recent non-empty color before start
                for (int j = start - 1; j >= 0; j--) {
                    char ccol = visColors.get(j);
                    if (ccol != '\0') return ccol;
                }
                return '\0';
            }
        }
        return '\0';
    }

    // Return the color code (format char) that applies to the first visible character in formatted text
    private static char getFirstVisibleCharColor(String formatted) {
        if (formatted == null || formatted.isEmpty()) return '\0';
        char current = '\0';
        for (int i = 0; i < formatted.length(); i++) {
            char c = formatted.charAt(i);
            if (c == '§') {
                if (i + 1 < formatted.length()) {
                    current = Character.toLowerCase(formatted.charAt(i+1));
                    i++;
                    continue;
                }
            }
            // first visible char found
            return current;
        }
        return '\0';
    }

    private static int mapChoiceToRenderColor(char[] opts, int idx) {
        char code = opts[Math.max(0, Math.min(2, idx))];
        switch (code) {
            case 'c': return 0xFF5555;
            case 'd': return 0xFF55FF;
            case '5': return 0xAA00AA;
            case 'a': return 0x55FF55;
            case '2': return 0x00AA00;
            case '8': return 0x555555;
            case '9': return 0x5555FF;
            case '1': return 0x0000AA;
            case '3': return 0x00AAAA;
            case 'e': return 0xFFFF55;
            case '6': return 0xFFAA00;
            case 'f': return 0xFFFFFF;
            default: return 0xFFFFFF;
        }
    }

    // Public helpers so GUI can render matching preview colors
    public static int getRedOptionColor(int idx) { return mapChoiceToRenderColor(RED_OPTS, idx); }
    public static int getGreenOptionColor(int idx) { return mapChoiceToRenderColor(GREEN_OPTS, idx); }
    public static int getBlueOptionColor(int idx) { return mapChoiceToRenderColor(BLUE_OPTS, idx); }
    public static int getYellowOptionColor(int idx) { return mapChoiceToRenderColor(YELLOW_OPTS, idx); }

    private static boolean isColorMatch(char lastColor, char[] opts, int choiceIdx) {
        if (lastColor == '\0') return false;
        char want = opts[Math.max(0, Math.min(2, choiceIdx))];
        return lastColor == want;
    }
}

