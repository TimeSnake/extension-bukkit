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
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdAfk implements CommandListener {


    public Set<User> users = new HashSet<>();
    public Set<User> toggledUsers = new HashSet<>();

    private Code.Permission perm;
    private Code.Permission togglePerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        if (cmd.getName().equalsIgnoreCase("afk") || cmd.getName().equalsIgnoreCase("awayfromkeyboard")) {
            if (!sender.hasPermission(this.perm)) {
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
            if (!sender.hasPermission(this.togglePerm)) {
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

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("afk", "exbukkit.afk");
        this.togglePerm = plugin.createPermssionCode("afk", "exbukkit.afk.toggle");
    }

    public void afk(User user) {
        if (this.users.contains(user)) {
            return;
        }

        this.users.add(user);
        Server.broadcastMessage(Plugin.BUKKIT, user.getChatNameComponent()
                .append(Component.text(" is now afk", ExTextColor.PUBLIC)));

    }

    public void unAfk(User user) {
        if (!this.users.contains(user)) {
            return;
        }

        this.users.remove(user);
        Server.broadcastMessage(Plugin.BUKKIT, user.getChatNameComponent()
                .append(Component.text(" is no longer afk", ExTextColor.PUBLIC)));

    }

    public void toggleAfk(User user) {
        if (this.toggledUsers.contains(user)) {
            return;
        }

        this.toggledUsers.add(user);
        user.sendPluginMessage(Plugin.BUKKIT, Component.text(" disabled auto-afk", ExTextColor.PERSONAL));
    }

    public void unToggleAfk(User user) {
        if (!this.toggledUsers.contains(user)) {
            return;
        }

        this.toggledUsers.remove(user);
        user.sendPluginMessage(Plugin.BUKKIT, Component.text(" enabled auto-afk", ExTextColor.PERSONAL));
    }
}
