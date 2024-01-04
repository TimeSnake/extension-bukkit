/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;
import org.bukkit.World;

public class CmdWeather implements CommandListener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.weather");
  private final Code weatherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.weather.weather");
  private final Code sunPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.weather.sun");
  private final Code rainPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.weather.rain");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    switch (cmd.getName()) {
      case "weather" -> this.handleCmdWeather(sender, args);
      case "sun" -> {
        if (args.isLengthEquals(1, false)) {
          this.sun(sender, args.get(0));
        } else if (args.isLengthEquals(0, false)) {
          this.sun(sender, null);
        } else {
          sender.sendMessageTooManyArguments();
        }
      }
      case "rain", "storm", "thunder" -> {
        if (args.isLengthEquals(1, false)) {
          this.rain(sender, args.get(0));
        } else if (args.isLengthEquals(0, false)) {
          this.rain(sender, null);
        } else {
          sender.sendMessageTooManyArguments();
        }
      }
    }
  }

  public void handleCmdWeather(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthHigherEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Change weather in world", "weather <clear/rain/sun> [world]");
      return;
    }

    if (!sender.hasPermission(this.weatherPerm)) {
      return;
    }

    Argument world = null;
    if (args.isLengthEquals(2, false)) {
      world = args.get(1);
    }

    switch (args.get(0).toLowerCase()) {
      case "clear", "sun" -> this.sun(sender, world);
      case "rain", "storm", "thunder" -> this.rain(sender, world);
      default -> {
        sender.sendMessageWeatherTypeNotExist(args.get(0).getString());
        sender.sendTDMessageCommandHelp("Change weather in world", "weather <clear/rain/sun> " + "[world]");
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion(this.rainPerm, "rain")
            .addArgument(Completion.ofWorldNames()))
        .addArgument(new Completion(this.sunPerm, "sun", "clear")
            .addArgument(Completion.ofWorldNames()));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void sun(Sender sender, Argument arg) {
    if (!sender.hasPermission(this.sunPerm)) {
      return;
    }

    if (arg != null) {
      if (arg.isWorldName(true)) {
        ExWorld world = arg.toWorld();
        world.setStorm(false);
        sender.sendPluginMessage(Component.text("Changed weather to ", ExTextColor.PERSONAL)
            .append(Component.text("sun ", ExTextColor.PERSONAL))
            .append(Component.text("in world ", ExTextColor.PERSONAL))
            .append(Component.text(world.getName(), ExTextColor.VALUE)));
      }
    } else if (sender.isPlayer(false)) {
      World world = sender.getPlayer().getWorld();
      world.setStorm(false);
      sender.sendPluginMessage(Component.text("Changed weather to ", ExTextColor.PERSONAL)
          .append(Component.text("sun ", ExTextColor.VALUE))
          .append(Component.text("in world ", ExTextColor.PERSONAL))
          .append(Component.text(world.getName(), ExTextColor.VALUE)));
    } else {
      sender.sendTDMessageCommandHelp("Set sun in world", "sun <world>");
    }
  }

  public void rain(Sender sender, Argument arg) {
    if (!sender.hasPermission(this.rainPerm)) {
      return;
    }

    if (arg != null) {
      if (arg.isWorldName(true)) {
        ExWorld world = arg.toWorld();
        world.setStorm(true);
        sender.sendPluginMessage(Component.text("Changed weather to ", ExTextColor.PERSONAL)
            .append(Component.text("rain ", ExTextColor.VALUE))
            .append(Component.text("in world ", ExTextColor.PERSONAL))
            .append(Component.text(world.getName(), ExTextColor.VALUE)));
      }
    } else if (sender.isPlayer(false)) {
      World world = sender.getPlayer().getWorld();
      world.setStorm(true);
      sender.sendPluginMessage(Component.text("Changed weather to ", ExTextColor.PERSONAL)
          .append(Component.text("rain ", ExTextColor.PERSONAL))
          .append(Component.text("in world ", ExTextColor.PERSONAL))
          .append(Component.text(world.getName(), ExTextColor.VALUE)));
    } else {
      sender.sendTDMessageCommandHelp("Set rain in world", "rain <world>");
    }
  }

}
