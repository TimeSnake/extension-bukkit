/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;

public class CmdNightVision implements CommandListener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.nightvision");
  private final Code otherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.nightvision.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd,
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
        user.addPotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false);
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
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(Completion.ofPlayerNames().permission(this.otherPerm));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
