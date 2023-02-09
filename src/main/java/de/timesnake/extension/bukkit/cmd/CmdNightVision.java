/*
 * Copyright (C) 2023 timesnake
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
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;

public class CmdNightVision implements CommandListener {

    private Code perm;
    private Code otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd,
            Arguments<Argument> args) {
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
                sender.sendPluginMessage(
                        Component.text("Disabled night vision", ExTextColor.PERSONAL));
            } else {
                user.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                sender.sendPluginMessage(
                        Component.text("Enabled night vision", ExTextColor.PERSONAL));
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
                other.sendPluginMessage(Plugin.BUKKIT,
                        Component.text("Disabled night vision", ExTextColor.PERSONAL));
                sender.sendPluginMessage(
                        Component.text("Disabled night vision for ", ExTextColor.PERSONAL)
                                .append(other.getChatNameComponent()));
            } else {
                other.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                other.sendPluginMessage(Plugin.BUKKIT,
                        Component.text("Enabled night vision", ExTextColor.PERSONAL));
                sender.sendPluginMessage(
                        Component.text("Enabled night vision for ", ExTextColor.PERSONAL)
                                .append(other.getChatNameComponent()));
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd,
            Arguments<Argument> args) {
        if (args.length() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("exbukkit.nightvision");
        this.otherPerm = plugin.createPermssionCode("exbukkit.nightvision.other");
    }
}
