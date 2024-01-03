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
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import de.timesnake.library.extension.util.chat.Code;
import net.kyori.adventure.text.Component;

public class CmdFly implements CommandListener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.fly");
  private final Code otherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.fly.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
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
          Component.text((fly ? "Enabled" : "Disabled") + " flying by ",
                  ExTextColor.PERSONAL)
              .append(sender.getChatName()));
      sender.sendPluginMessage(Component.text((fly ? "Enabled" : "Disabled") + " flying for ",
              ExTextColor.PERSONAL)
          .append(user.getChatNameComponent()));
    } else {
      sender.sendPluginMessage(Component.text((fly ? "Enabled" : "Disabled") + " flying",
          ExTextColor.PERSONAL));
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
