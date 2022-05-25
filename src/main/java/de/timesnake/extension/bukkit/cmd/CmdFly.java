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

public class CmdFly implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user = null;

        if (args.isLengthEquals(1, false)) {
            if (!sender.hasPermission("exbukkit.fly.other", 932) || !args.get(0).isPlayerName(true)) {
                return;
            }

            user = args.get(0).toUser();
        }

        if (user == null) {
            if (!sender.isPlayer(true)) {
                return;
            }

            if (!sender.hasPermission("exbukkit.fly", 931)) {
                return;
            }

            user = sender.getUser();
        }

        boolean fly = !user.getPlayer().getAllowFlight();

        user.setAllowFlight(fly);
        user.setFlying(fly);

        if (!sender.getUser().equals(user)) {
            user.sendPluginMessage(Plugin.BUKKIT,
                    ChatColor.PERSONAL + (fly ? "Enabled" : "Disabled") + " flying by " + ChatColor.VALUE + sender.getChatName());
            sender.sendPluginMessage(ChatColor.PERSONAL + (fly ? "Enabled" : "Disabled") + " flying for " + ChatColor.VALUE + user.getChatName());
        } else {
            sender.sendPluginMessage(ChatColor.PERSONAL + (fly ? "Enabled" : "Disabled") + " flying");
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }
}
