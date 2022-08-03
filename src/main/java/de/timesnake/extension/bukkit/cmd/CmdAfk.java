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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdAfk implements CommandListener {


    public Set<User> users = new HashSet<>();
    public Set<User> toggledUsers = new HashSet<>();

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        if (cmd.getName().equalsIgnoreCase("afk") || cmd.getName().equalsIgnoreCase("awayfromkeyboard")) {
            if (!sender.hasPermission("exbukkit.afk", 938)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            User user = sender.getUser();

            if (!this.users.contains(user)) {
                this.afk(user);
            } else {
                this.unAfk(user);
            }
        } else if (cmd.getName().equalsIgnoreCase("afktoggle")) {
            if (!sender.hasPermission("exbukkit.afk.toggle", 939)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            User user = sender.getUser();
            if (!this.toggledUsers.contains(user)) {
                this.toggleAfk(user);
            } else {
                this.unToggleAfk(user);
            }
        }

    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return List.of();
    }

    public void afk(User user) {
        if (this.users.contains(user)) {
            return;
        }

        this.users.add(user);
        Server.broadcastMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PUBLIC + " is now afk");

    }

    public void unAfk(User user) {
        if (!this.users.contains(user)) {
            return;
        }

        this.users.remove(user);
        Server.broadcastMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PUBLIC + " is no " +
                "longer afk");

    }

    public void toggleAfk(User user) {
        if (this.toggledUsers.contains(user)) {
            return;
        }

        this.toggledUsers.add(user);
        user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + " disabled auto-afk");
    }

    public void unToggleAfk(User user) {
        if (!this.toggledUsers.contains(user)) {
            return;
        }

        this.toggledUsers.remove(user);
        user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + " enabled auto-afk");
    }
}
