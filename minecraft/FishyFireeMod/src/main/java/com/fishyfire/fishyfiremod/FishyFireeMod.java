package com.fishyfire.fishyfiremod;

import com.fishyfire.fishyfiremod.commands.CounterCommand;
import com.fishyfire.fishyfiremod.commands.ExampleCommand;
// ...removed HideEmCommand import
import com.fishyfire.fishyfiremod.config.ConfigHandler;
import com.fishyfire.fishyfiremod.events.KeybindListener;
// ...removed ChatFilterListener import
import com.fishyfire.fishyfiremod.hud.CounterHUD;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = FishyFireeMod.MODID,
        name = FishyFireeMod.MODNAME,
        version = FishyFireeMod.VERSION)
public class FishyFireeMod {

    public static final String MODID = "fishyfireemod";
    public static final String MODNAME = "FishyFiree Mod";
    public static final String VERSION = "1.1";
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new ExampleCommand());
        ClientCommandHandler.instance.registerCommand(new CounterCommand());
        ClientCommandHandler.instance.registerCommand(new com.fishyfire.fishyfiremod.commands.PlayerTrackerCommand());
        // chat-filter related commands and listeners removed

        MinecraftForge.EVENT_BUS.register(new KeybindListener());
        MinecraftForge.EVENT_BUS.register(new CounterHUD());
        MinecraftForge.EVENT_BUS.register(new com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD());
        // chat-filter related listeners removed

        if (Loader.isModLoaded("patcher")) {
            // compatibility code for Patcher mod
        }

    }
}
