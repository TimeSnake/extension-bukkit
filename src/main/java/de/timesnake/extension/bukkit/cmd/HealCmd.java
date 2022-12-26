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

public class HealCmd implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.isLengthEquals(0, false)) {
            if (sender.hasPermission(this.perm) && sender.isPlayer(true)) {
                sender.getUser().heal();
                sender.sendPluginMessage(Component.text("Healed", ExTextColor.PERSONAL));
            }
        } else if (args.isLengthEquals(1, true)) {
            if (args.get(0).isPlayerName(true) && sender.hasPermission(this.otherPerm)) {
                User other = args.get(0).toUser();
                other.heal();
                sender.sendPluginMessage(Component.text("Healed ", ExTextColor.PERSONAL)
                        .append(other.getChatNameComponent()));
                other.sendPluginMessage(Plugin.BUKKIT, Component.text("Healed", ExTextColor.PERSONAL));
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return null;
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("hel", "exbukkit.heal");
        this.otherPerm = plugin.createPermssionCode("hel", "exbukkit.heal.other");
    }
}
