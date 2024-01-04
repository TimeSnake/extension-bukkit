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

public class CmdSpeed implements CommandListener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.speed.<mode>");
  private final Code otherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.speed.<mode>.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    switch (cmd.getName().toLowerCase()) {
      case "speed" -> {
        if (args.isLengthHigherEquals(1, true)) {
          if (args.get(0).isFloat(true)) {
            User user = sender.getUser();
            if (args.isLengthEquals(2, false)) {
              if (args.get(1).isPlayerName(true)) {
                user = args.get(1).toUser();
              }
            }
            if (user.getPlayer().isFlying()) {
              this.set(sender, user, args.get(0).toFloat(), Type.FLY);
            } else {
              this.set(sender, user, args.get(0).toFloat(), Type.WALK);
            }
          }
        } else {
          sender.sendTDMessageCommandHelp("Set speed", "speed <speed>");
          sender.sendTDMessageCommandHelp("Set speed for player",
              "speed <speed> <player>");
        }
      }
      case "speedfly" -> {
        if (args.isLengthHigherEquals(1, true)) {
          if (args.get(0).isFloat(true)) {
            User user = sender.getUser();
            if (args.isLengthEquals(2, false)) {
              if (args.get(1).isPlayerName(true)) {
                user = args.get(1).toUser();
              }
            }
            this.set(sender, user, args.get(0).toFloat(), Type.FLY);
          }
        } else {
          sender.sendTDMessageCommandHelp("Set fly speed", "speedfly <speed>");
          sender.sendTDMessageCommandHelp("Set fly speed for player",
              "speedfly <speed> <player>");
        }
      }
      case "speedwalk" -> {
        if (args.isLengthHigherEquals(1, true)) {
          if (args.get(0).isFloat(true)) {
            User user = sender.getUser();
            if (args.isLengthEquals(2, false)) {
              if (args.get(1).isPlayerName(true)) {
                user = args.get(1).toUser();
              }
            }
            this.set(sender, user, args.get(0).toFloat(), Type.WALK);

          }
        } else {
          sender.sendTDMessageCommandHelp("Set walk speed", "speedwalk <speed>");
          sender.sendTDMessageCommandHelp("Set walk speed for player",
              "speedwalk <speed> <player>");
        }
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("1", "2", "1.2", "1.5")
            .addArgument(Completion.ofPlayerNames().permission(this.otherPerm)));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  private void set(Sender sender, User user, float speed, Type mode) {
    if (sender.getName().equals(user.getName())) {
      if (!sender.hasPermission(this.perm)) {
        return;
      }

      if (setWithPermission(sender, user, speed, mode)) {
        return;
      }

      user.sendPluginMessage(Plugin.BUKKIT,
          Component.text("Updated " + mode.name().toLowerCase() +
                  "speed to ", ExTextColor.PERSONAL)
              .append(Component.text(speed, ExTextColor.VALUE)));
    } else {
      if (sender.hasPermission(this.otherPerm)) {
        if (setWithPermission(sender, user, speed, mode)) {
          return;
        }

        sender.sendPluginMessage(
            Component.text("Updated " + mode.name().toLowerCase() + "speed from ",
                    ExTextColor.PERSONAL)
                .append(user.getChatNameComponent())
                .append(Component.text(" to ", ExTextColor.PERSONAL))
                .append(Component.text(speed, ExTextColor.VALUE)));
        user.sendPluginMessage(Plugin.BUKKIT,
            Component.text("Updated " + mode.name().toLowerCase() +
                    "speed by ", ExTextColor.PERSONAL)
                .append(sender.getChatName())
                .append(Component.text(" to ", ExTextColor.PERSONAL))
                .append(Component.text(speed, ExTextColor.VALUE)));
      }

    }
  }

  private boolean setWithPermission(Sender sender, User user, float speed, Type mode) {
    if (!(speed <= 5 || speed > 0)) {
      sender.sendPluginMessage(Component.text("Top speed is 5", ExTextColor.WARNING));
      return true;
    }

    if (mode == null) {
      mode = Type.WALK;
    }

    if (mode.equals(Type.FLY)) {
      user.getPlayer().setFlySpeed((float) (speed * 0.2));
    } else {
      user.getPlayer().setWalkSpeed((float) (speed * 0.2));
    }

    return false;
  }

  enum Type {
    FLY,
    WALK
  }
}
