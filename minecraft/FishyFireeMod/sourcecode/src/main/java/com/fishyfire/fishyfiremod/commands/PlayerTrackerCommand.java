package com.fishyfire.fishyfiremod.commands;

import com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class PlayerTrackerCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "playertracker";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/playertracker <toggle|settings>";
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
            sender.addChatMessage(new ChatComponentText("§eUsage: /playertracker <toggle|settings>"));
            return;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            PlayerTrackerHUD.toggle();
            String status = PlayerTrackerHUD.isEnabled() ? "§aEnabled" : "§cDisabled";
            // show message (no sound)
            sender.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fPlayerTracker " + status));
        } else if (args[0].equalsIgnoreCase("settings")) {
            sender.addChatMessage(new ChatComponentText("§d[FishyFireeMod] §fOpening player tracker settings..."));
            // request client open; use existing opener and flag the player tracker request
            com.fishyfire.fishyfiremod.events.GuiOpener.requestOpen();
            com.fishyfire.fishyfiremod.hud.PlayerTrackerHUD.requestOpenSettings();
        } else {
            sender.addChatMessage(new ChatComponentText("§eUsage: /playertracker <toggle|settings>"));
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

