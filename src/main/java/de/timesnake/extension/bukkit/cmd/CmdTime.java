/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;
import org.bukkit.World;

public class CmdTime implements CommandListener {

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.time");
  private final Code dayPerm = Plugin.SERVER.createPermssionCode("exbukkit.time.day");
  private final Code nightPerm = Plugin.SERVER.createPermssionCode("exbukkit.time.noon");
  private final Code noonPerm = Plugin.SERVER.createPermssionCode("exbukkit.time.night");
  private final Code setPerm = Plugin.SERVER.createPermssionCode("exbukkit.time.set");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    switch (cmd.getName()) {
      case "time" -> this.handleCmdTime(sender, args);
      case "day" -> {
        if (sender.hasPermission(this.dayPerm)) {
          if (args.isLengthEquals(1, false)) {
            this.setDay(sender, args.get(0));
          } else if (sender.isPlayer(false)) {
            this.setDay(sender,
                new Argument(sender, sender.getPlayer().getWorld().getName()));
          } else {
            sender.sendTDMessageCommandHelp("Set day in world", "day <world>");
          }
        }
      }
      case "night" -> {
        if (sender.hasPermission(this.nightPerm)) {
          if (args.isLengthEquals(1, false)) {
            this.setNight(sender, args.get(0));
          } else if (sender.isPlayer(false)) {
            this.setNight(sender,
                new Argument(sender, sender.getPlayer().getWorld().getName()));
          } else {
            sender.sendTDMessageCommandHelp("Set night in world", "night <world>");
          }
        }
      }
      case "noon" -> {
        if (sender.hasPermission(this.noonPerm)) {
          if (args.isLengthEquals(1, false)) {
            this.setNoon(sender, args.get(0));
          } else if (sender.isPlayer(false)) {
            this.setNoon(sender,
                new Argument(sender, sender.getPlayer().getWorld().getName()));
          } else {
            sender.sendTDMessageCommandHelp("Set noon in world", "noon <world>");
          }
        }
      }
    }
  }

  public void handleCmdTime(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthHigherEquals(1, false)) {
      if (sender.isPlayer(false)) {
        long ticks = sender.getPlayer().getWorld().getTime();
        sender.sendPluginMessage(Component.text("Current time: ", ExTextColor.PERSONAL)
            .append(Component.text(ticks, ExTextColor.VALUE))
            .append(Component.text(" ticks, ", ExTextColor.PERSONAL))
            .append(Component.text(new Argument(sender, String.valueOf(ticks)).toTime(),
                ExTextColor.VALUE)));
        return;
      } else {
        sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
      }
      return;
    }

    switch (args.getString(0).toLowerCase()) {
      case "set" -> {
        if (!args.isLengthHigherEquals(2, true)) {
          sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
          return;
        }
        if (!sender.hasPermission(this.setPerm)) {
          return;
        }
        if (args.isLengthHigherEquals(3, false)) {
          if (args.get(1).equalsIgnoreCase("day")) {
            this.setDay(sender, args.get(2));
          } else if (args.get(1).equalsIgnoreCase("night")) {
            this.setNight(sender, args.get(2));
          } else if (args.get(1).isInt(false)) {
            this.set(sender, args.get(2), args.get(1));
          } else if (args.get(1).isTime(true)) {
            this.set(sender, args.get(2),
                new Argument(sender, String.valueOf(args.get(1).toTicks())));
          }
        } else if (sender.isPlayer(false)) {

          World world = sender.getPlayer().getWorld();

          switch (args.getString(1).toLowerCase()) {
            case "day" -> this.setDay(sender, new Argument(sender, world.getName()));
            case "night" -> this.setNight(sender, new Argument(sender, world.getName()));
            case "noon" -> this.setNoon(sender, new Argument(sender, world.getName()));
            default -> {
              if (args.get(1).isInt(false)) {
                this.set(sender, new Argument(sender, world.getName()),
                    args.get(1));
              } else if (args.get(1).isTime(true)) {
                this.set(sender, new Argument(sender, world.getName()),
                    new Argument(sender,
                        String.valueOf(args.get(1).toTicks())));
              }
            }
          }
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
        }
      }
      case "day" -> {
        if (!sender.hasPermission(this.dayPerm)) {
          return;
        }
        if (args.isLengthHigherEquals(3, false)) {
          this.setDay(sender, args.get(2));
        } else if (sender.isPlayer(false)) {
          this.setDay(sender,
              new Argument(sender, sender.getPlayer().getWorld().getName()));
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
        }
      }
      case "night" -> {
        if (!sender.hasPermission(this.nightPerm)) {
          return;
        }
        if (args.isLengthHigherEquals(3, false)) {
          this.setNight(sender, args.get(2));
        } else if (sender.isPlayer(false)) {
          this.setNight(sender,
              new Argument(sender, sender.getPlayer().getWorld().getName()));
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
        }
      }
      case "noon" -> {
        if (!sender.hasPermission(this.noonPerm)) {
          return;
        }
        if (args.isLengthHigherEquals(3, false)) {
          this.setNoon(sender, args.get(2));
        } else if (sender.isPlayer(false)) {
          this.setNoon(sender,
              new Argument(sender, sender.getPlayer().getWorld().getName()));
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
        }
      }
      default -> sender.sendTDMessageCommandHelp("Set time in world", "time set <time> [world]");
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion()
        .addArgument(new Completion(this.dayPerm, "day"))
        .addArgument(new Completion(this.nightPerm, "night"))
        .addArgument(new Completion(this.setPerm, "set")
            .addArgument(new Completion("day", "night", "noon")
                .addArgument(Completion.ofWorldNames())));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void set(Sender sender, Argument arg0, Argument arg1) {
    if (sender.hasPermission(this.setPerm)) {
      if (arg0.isWorldName(true) && arg1.isInt(true)) {
        ExWorld world = arg0.toWorld();
        world.setTime(arg1.toInt());
        sender.sendPluginMessage(
            Component.text("Updated time in world ", ExTextColor.PERSONAL)
                .append(Component.text(world.getName(), ExTextColor.VALUE))
                .append(Component.text(" to ", ExTextColor.PERSONAL))
                .append(Component.text(arg1.toInt(), ExTextColor.VALUE))
                .append(Component.text(" " + "ticks, ", ExTextColor.PERSONAL))
                .append(Component.text(arg1.toTime(), ExTextColor.VALUE)));
      }
    }
  }

  public void setDay(Sender sender, Argument arg) {
    this.set(sender, arg, new Argument(sender, "1000"));
  }

  public void setNight(Sender sender, Argument arg) {
    this.set(sender, arg, new Argument(sender, "13000"));
  }

  public void setNoon(Sender sender, Argument arg) {
    this.set(sender, arg, new Argument(sender, "3000"));
  }

}
