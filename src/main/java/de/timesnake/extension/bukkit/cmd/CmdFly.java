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

import java.util.List;

public class CmdFly implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        User user = null;

        if (args.isLengthEquals(1, false)) {
            if (!sender.hasPermission(this.otherPerm) || !args.get(0).isPlayerName(true)) {
                return;
            }

            user = args.get(0).toUser();
        }

        if (user == null) {
            if (!sender.isPlayer(true)) {
                return;
            }

            if (!sender.hasPermission(this.perm)) {
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

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("fly", "exbukkit.fly");
        this.otherPerm = plugin.createPermssionCode("fly", "exbukkit.fly.other");
    }
}
