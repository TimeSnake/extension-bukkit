/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;

import java.util.List;

public class CmdGod implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user = null;

        if (args.isLengthEquals(0, false)) {
            if (!sender.isPlayer(true) || !sender.hasPermission(this.perm)) {
                return;
            }
            user = sender.getUser();

        } else if (args.isLengthEquals(1, true)) {
            if (sender.hasPermission(this.otherPerm)) {
                if (!args.get(0).isPlayerName(true)) {
                    return;
                }
                user = args.get(0).toUser();
            }
        } else {
            sender.sendMessageCommandHelp("Set god mode", "god [player]");
            return;
        }


        user.setInvulnerable(!user.isInvulnerable());
        if (!sender.isPlayer(false) || !sender.getUser().equals(user)) {
            sender.sendPluginMessage(Component.text((user.isInvulnerable() ? "Enabled" : "Disabled") + " god " +
                    "mode for ", ExTextColor.PERSONAL).append(user.getChatNameComponent()));
        }

        user.sendPluginMessage(Plugin.BUKKIT, Component.text((user.isInvulnerable() ? "Enabled" : "Disabled") + " god mode", ExTextColor.PERSONAL));
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("god", "exbukkit.god");
        this.otherPerm = plugin.createPermssionCode("god", "exbukkit.god.other");
    }
}
