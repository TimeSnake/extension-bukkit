package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;

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
                    Component.text((fly ? "Enabled" : "Disabled") + " flying by ", ExTextColor.PERSONAL)
                            .append(sender.getChatName()));
            sender.sendPluginMessage(Component.text((fly ? "Enabled" : "Disabled") + " flying for ", ExTextColor.PERSONAL)
                    .append(user.getChatNameComponent()));
        } else {
            sender.sendPluginMessage(Component.text((fly ? "Enabled" : "Disabled") + " flying", ExTextColor.PERSONAL));
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
