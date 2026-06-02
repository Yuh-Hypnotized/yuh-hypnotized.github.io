package com.fishyfire.fishyfiremod.events;

import com.fishyfire.fishyfiremod.hud.CounterHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.fishyfire.fishyfiremod.gui.GuiCounterSettings;
import org.lwjgl.input.Keyboard;

public class KeybindListener {

        // you need to register your keybind for it to show up in the settings menu
    private final KeyBinding counterToggleKeybind = new KeyBinding("Toggle counters", Keyboard.KEY_NONE, "FishyFiree Mod");
    private final KeyBinding playerTrackerToggleKeybind = new KeyBinding("Toggle player tracker", Keyboard.KEY_NONE, "FishyFiree Mod");

    public KeybindListener() {
        ClientRegistry.registerKeyBinding(counterToggleKeybind);
        ClientRegistry.registerKeyBinding(playerTrackerToggleKeybind);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        // this method runs everytime a key on the keyboard is pressed / unpressed

        if (counterToggleKeybind.isPressed()) { // using isPressed() will return true once when the key is pressed
            CounterHUD.toggle();
            if (CounterHUD.isShowToggleMessage()) {
                boolean on = CounterHUD.isEnabled();
                String status = on ? "§aEnabled" : "§cDisabled";
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fCounter HUD " + status));
            }
        }
        if (playerTrackerToggleKeybind.isPressed()) {
            // toggle player tracker HUD and send a chat message (no sound)
            com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD.toggle();
            String status = com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD.isEnabled() ? "§aEnabled" : "§cDisabled";
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fPlayerTracker " + status));
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // run on client thread; open GUI when requested
        if (com.fishyfire.fishyfiremod.events.GuiOpener.consumeRequest()) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                // if player tracker asked to open, prefer that
                if (com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD.consumeOpenSettings()) {
                    mc.displayGuiScreen(new com.fishyfire.fishyfiremod.gui.GuiPlayerTrackerSettings());
                } else {
                    mc.displayGuiScreen(new GuiCounterSettings());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

        }
