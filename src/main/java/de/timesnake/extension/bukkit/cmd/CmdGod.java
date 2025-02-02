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

public class CmdGod implements CommandListener {

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.god");
  private final Code otherPerm = Plugin.SERVER.createPermssionCode("exbukkit.god.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd,
      Arguments<Argument> args) {

    User user = null;

    if (args.isLengthEquals(0, false)) {
      if (!sender.isPlayer(true) || !sender.hasPermission(this.perm)) {
        return;
      }
      user = sender.getUser();

    } else if (args.isLengthEquals(1, true)) {
      if (sender.hasPermission(this.otherPerm)) {
        if (!args.get(0).isPlayerName(true)) {
          return;
        }
        user = args.get(0).toUser();
      }
    } else {
      sender.sendTDMessageCommandHelp("Set god mode", "god [player]");
      return;
    }

    user.setInvulnerable(!user.isInvulnerable());
    if (!sender.isPlayer(false) || !sender.getUser().equals(user)) {
      sender.sendPluginMessage(
          Component.text((user.isInvulnerable() ? "Enabled" : "Disabled") + " god " +
              "mode for ", ExTextColor.PERSONAL).append(user.getChatNameComponent()));
    }

    user.sendPluginMessage(Plugin.SERVER,
        Component.text((user.isInvulnerable() ? "Enabled" : "Disabled") + " god mode",
            ExTextColor.PERSONAL));
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
