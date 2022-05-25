package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class CmdGod implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user = null;

        if (args.isLengthEquals(0, false)) {
            if (!sender.isPlayer(true)) {
                return;
            }
            user = sender.getUser();

        } else if (args.isLengthEquals(1, true)) {
            if (sender.hasPermission("exbukkit.god.other", 937)) {
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
            sender.sendPluginMessage(ChatColor.PERSONAL + (user.isInvulnerable() ? "Enabled" : "Disabled") + " god " +
                    "mode for " + ChatColor.VALUE + user.getChatName());
        }

        user.sendPluginMessage(Plugin.BUKKIT,
                ChatColor.PERSONAL + (user.isInvulnerable() ? "Enabled" : "Disabled") + " god mode");
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }
}
