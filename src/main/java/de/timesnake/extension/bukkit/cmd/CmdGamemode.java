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
import org.bukkit.GameMode;

public class CmdGamemode implements CommandListener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.gamemode");
  private final Code otherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.gamemode.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    this.handleCmdGamemode(sender, args);
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("survival", "creative", "adventure", "spectator", "1", "2", "3", "4")
            .addArgument(Completion.ofPlayerNames().permission(this.otherPerm)));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void handleCmdGamemode(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthHigherEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Set gamemode", "gm <mode>");
      sender.sendTDMessageCommandHelp("Set gamemode for player", "gm <mode> <player>");
      return;
    }

    User user = sender.getUser();
    if (args.isLengthEquals(2, false)) {
      if (args.get(1).isPlayerName(true)) {
        user = args.get(1).toUser();
      } else {
        return;
      }
    }

    String name;
    GameMode gameMode;

    switch (args.get(0).toLowerCase()) {
      case "survival", "0" -> {
        gameMode = GameMode.SURVIVAL;
        name = "Survival";
      }
      case "creative", "1" -> {
        gameMode = GameMode.CREATIVE;
        name = "Creative";
      }
      case "adventure", "2" -> {
        gameMode = GameMode.ADVENTURE;
        name = "Adventure";
      }
      case "spectator", "3" -> {
        gameMode = GameMode.SPECTATOR;
        name = "Spectator";
      }
      default -> {
        sender.sendMessageGamemodeNotExist(args.get(0).getString());
        return;
      }
    }

    if (sender.getName().equals(user.getName())) {
      if (!sender.hasPermission(this.perm)) {
        return;
      }

    } else {
      if (!sender.hasPermission(this.otherPerm)) {
        return;
      }

      if (!sender.hasGroupRankLower(sender.getUser().getUniqueId())) {
        return;
      }
      sender.sendPluginMessage(Component.text("Updated gamemode from ", ExTextColor.PERSONAL)
          .append(user.getChatNameComponent())
          .append(Component.text(" to ", ExTextColor.PERSONAL))
          .append(Component.text(name, ExTextColor.VALUE)));
    }

    user.setGameMode(gameMode);
    user.sendPluginMessage(Plugin.BUKKIT,
        Component.text("Updated gamemode to ", ExTextColor.PERSONAL).append(Component.text(name, ExTextColor.VALUE)));
  }
}
