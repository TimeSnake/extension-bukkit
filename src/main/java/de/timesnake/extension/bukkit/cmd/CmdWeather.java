/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.World;

import java.util.List;

public class CmdWeather implements CommandListener {

    private Code.Permission weatherPerm;
    private Code.Permission sunPerm;
    private Code.Permission rainPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        switch (cmd.getName()) {
            case "weather":
                this.handleCmdWeather(sender, args);
                break;

            case "sun":
                if (args.isLengthEquals(1, false)) {
                    this.sun(sender, args.get(0));
                } else if (args.isLengthEquals(0, false)) {
                    this.sun(sender, null);
                } else {
                    sender.sendMessageTooManyArguments();
                }
                break;

            case "rain":
            case "storm":
            case "thunder":
                if (args.isLengthEquals(1, false)) {
                    this.rain(sender, args.get(0));
                } else if (args.isLengthEquals(0, false)) {
                    this.rain(sender, null);
                } else {
                    sender.sendMessageTooManyArguments();
                }

                break;
        }
    }

    public void handleCmdWeather(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            sender.sendMessageCommandHelp("Change weather in world", "weather <clear/rain/sun> [world]");
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
                sender.sendMessageCommandHelp("Change weather in world", "weather <clear/rain/sun> " + "[world]");
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (cmd.getName().equalsIgnoreCase("weather")) {
            return null;
        }

        if (args.getLength() == 1) {
            return List.of("clear", "sun", "rain", "storm", "thunder");
        } else if (args.getLength() == 2) {
            return Server.getCommandManager().getTabCompleter().getWorldNames();
        }
        return null;
    }

    @Override
    public void loadCodes(Plugin plugin) {
        this.weatherPerm = plugin.createPermssionCode("wea", "exbukkit.weather.weather");
        this.sunPerm = plugin.createPermssionCode("wea", "exbukkit.weather.sun");
        this.rainPerm = plugin.createPermssionCode("wea", "exbukkit.weather.rain");
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
            sender.sendMessageCommandHelp("Set sun in world", "sun <world>");
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
            sender.sendMessageCommandHelp("Set rain in world", "rain <world>");
        }
    }

}
