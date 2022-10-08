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
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CmdNightVision implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.isLengthEquals(0, false)) {
            if (!sender.hasPermission(this.perm)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            User user = sender.getUser();

            if (user.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                user.removePotionEffect(PotionEffectType.NIGHT_VISION);
                sender.sendPluginMessage(Component.text("Disabled night vision", ExTextColor.PERSONAL));
            } else {
                user.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                sender.sendPluginMessage(Component.text("Enabled night vision", ExTextColor.PERSONAL));
            }
        } else if (args.isLengthEquals(1, true)) {
            if (!sender.hasPermission(this.otherPerm)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            User other = args.get(0).toUser();

            if (other.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                other.removePotionEffect(PotionEffectType.NIGHT_VISION);
                other.sendPluginMessage(Plugin.BUKKIT, Component.text("Disabled night vision", ExTextColor.PERSONAL));
                sender.sendPluginMessage(Component.text("Disabled night vision for ", ExTextColor.PERSONAL)
                        .append(other.getChatNameComponent()));
            } else {
                other.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                other.sendPluginMessage(Plugin.BUKKIT, Component.text("Enabled night vision", ExTextColor.PERSONAL));
                sender.sendPluginMessage(Component.text("Enabled night vision for ", ExTextColor.PERSONAL)
                        .append(other.getChatNameComponent()));
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.length() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("cnv", "exbukkit.nightvision");
        this.otherPerm = plugin.createPermssionCode("cnv", "exbukkit.nightvision.other");
    }
}
