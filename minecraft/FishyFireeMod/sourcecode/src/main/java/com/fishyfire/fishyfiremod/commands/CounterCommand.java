package com.fishyfire.fishyfiremod.commands;

import com.fishyfire.fishyfiremod.hud.CounterHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CounterCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "counter";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/counter <toggle|settings>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§eUsage: /counter <toggle|settings>"));
            return;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            CounterHUD.toggle();
            // Compose message: prefix (pink) + Counter HUD (white) + status (green/red)
            if (CounterHUD.isShowToggleMessage()) {
                boolean on = CounterHUD.isEnabled();
                String status = on ? "§aEnabled" : "§cDisabled";
                sender.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fCounter HUD " + status));
            }
        } else if (args[0].equalsIgnoreCase("settings")) {
            // Debug: notify command reached settings branch
            sender.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fOpening counter settings..."));
            // Request the client to open the GUI on the next client tick. This avoids
            // any client-only class loading or threading issues when the command runs.
            com.fishyfire.fishyfiremod.events.GuiOpener.requestOpen();
        } else {
            sender.addChatMessage(new ChatComponentText("§eUsage: /counter <toggle|settings>"));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[]{"toggle", "settings"});
        }
        return null;
    }
}
