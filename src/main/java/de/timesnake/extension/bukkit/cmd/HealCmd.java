/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;

public class HealCmd implements CommandListener {

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.heal");
  private final Code otherPerm = Plugin.SERVER.createPermssionCode("exbukkit.heal.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (args.isLengthEquals(0, false)) {
      if (sender.hasPermission(this.perm) && sender.isPlayer(true)) {
        sender.getUser().heal();
        sender.sendPluginMessage(Component.text("Healed", ExTextColor.PERSONAL));
      }
    } else if (args.isLengthEquals(1, true)) {
      if (args.get(0).isPlayerName(true) && sender.hasPermission(this.otherPerm)) {
        User other = args.get(0).toUser();
        other.heal();
        sender.sendPluginMessage(Component.text("Healed ", ExTextColor.PERSONAL)
            .append(other.getChatNameComponent()));
        other.sendPluginMessage(Plugin.SERVER,
            Component.text("Healed", ExTextColor.PERSONAL));
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
