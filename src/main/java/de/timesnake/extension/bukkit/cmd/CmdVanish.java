package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserJoinEvent;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdVanish implements CommandListener, Listener {

    private final Set<User> users = new HashSet<>();

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user;

        if (args.isLengthEquals(0, false) && sender.isPlayer(true)) {
            if (!sender.hasPermission("exbukkit.vanish", 933)) {
                return;
            }
            user = sender.getUser();
        } else if (args.isLengthEquals(1, true)) {
            if (!sender.hasPermission("exbukkit.fly.other", 934)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            user = args.get(0).toUser();
        } else {
            sender.sendMessageTooManyArguments();
            return;
        }

        if (this.users.contains(user)) {

            for (User u : Server.getUsers()) {
                u.showUser(user);
            }

            this.users.remove(user);

            if (!sender.getUser().equals(user)) {
                sender.sendPluginMessage(ChatColor.PERSONAL + "Disabled vanish for " + ChatColor.VALUE + user.getChatName());
            }
            user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Disabled vanish");
        } else {

            for (User u : Server.getUsers()) {
                if (!u.hasPermission("exbukkit.vanish.see")) { //code: 935
                    u.hideUser(user);
                }
            }

            this.users.add(user);

            if (!sender.getUser().equals(user)) {
                sender.sendPluginMessage(ChatColor.PERSONAL + "Enabled vanish for " + ChatColor.VALUE + user.getChatName());
            }
            user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Enabled vanish");

        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }

    @EventHandler
    public void onPlayerJoin(UserJoinEvent e) {
        User user = e.getUser();
        if (!user.hasPermission("exbukkit.vanish.see")) {
            for (User u : Server.getUsers()) {
                if (this.users.contains(u)) {
                    user.hideUser(u);
                } else {
                    user.showUser(u);
                }
            }
        } else {
            for (User u : Server.getUsers()) {
                user.showUser(u);
            }
        }
    }

}
