package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ChatColor;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CmdNightVision implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.isLengthEquals(0, false)) {
            if (!sender.hasPermission("exbukkit.nightvision", 958)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            User user = sender.getUser();

            if (user.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                user.removePotionEffect(PotionEffectType.NIGHT_VISION);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Disabled night vision");
            } else {
                user.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Enabled night vision");
            }
        } else if (args.isLengthEquals(1, true)) {
            if (!sender.hasPermission("exbukkit.nightvision.other", 959)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            User other = args.get(0).toUser();

            if (other.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                other.removePotionEffect(PotionEffectType.NIGHT_VISION);
                other.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Disabled night vision");
                sender.sendPluginMessage(ChatColor.PERSONAL + "Disabled night vision for " + other.getChatName());
            } else {
                other.addPotionEffect(PotionEffectType.NIGHT_VISION, 1);
                other.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Enabled night vision");
                sender.sendPluginMessage(ChatColor.PERSONAL + "Enabled night vision for " + other.getChatName());
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
}
