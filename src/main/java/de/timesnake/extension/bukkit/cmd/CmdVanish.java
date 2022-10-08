/*
 * extension-bukkit.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserJoinEvent;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdVanish implements CommandListener, Listener {

    private final Set<User> users = new HashSet<>();

    private Code.Permission perm;
    private Code.Permission otherPerm;
    private Code.Permission seePerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user;

        if (args.isLengthEquals(0, false) && sender.isPlayer(true)) {
            if (!sender.hasPermission(this.perm)) {
                return;
            }
            user = sender.getUser();
        } else if (args.isLengthEquals(1, true)) {
            if (!sender.hasPermission(this.otherPerm)) {
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
                sender.sendPluginMessage(Component.text("Disabled vanish for ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
            }
            user.sendPluginMessage(Plugin.BUKKIT, Component.text("Disabled vanish", ExTextColor.PERSONAL));
        } else {

            for (User u : Server.getUsers()) {
                if (!u.hasPermission(this.seePerm.getPermission())) {
                    u.hideUser(user);
                }
            }

            this.users.add(user);

            if (!sender.getUser().equals(user)) {
                sender.sendPluginMessage(Component.text("Enabled vanish for ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
            }
            user.sendPluginMessage(Plugin.BUKKIT, Component.text("Enabled vanish", ExTextColor.PERSONAL));

        }
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
        this.perm = plugin.createPermssionCode("van", "exbukkit.vanish");
        this.otherPerm = plugin.createPermssionCode("van", "exbukkit.vanish.other");
        this.seePerm = plugin.createPermssionCode("van", "exbukkit.vanish.see");
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
