package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.library.basic.util.cmd.Arguments;
import de.timesnake.library.basic.util.cmd.ExCommand;
import org.bukkit.World;

import java.util.List;

public class CmdWeather implements CommandListener {

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

        if (!sender.hasPermission("exbukkit.weather.weather", 927)) {
            return;
        }

        Argument world = null;
        if (args.isLengthEquals(2, false)) {
            world = args.get(1);
        }

        switch (args.get(0).toLowerCase()) {
            case "clear":
            case "sun":
                this.sun(sender, world);
                break;
            case "rain":
            case "storm":
            case "thunder":
                this.rain(sender, world);
                break;
            default:
                sender.sendMessageWeatherTypeNotExist(args.get(0).getString());
                sender.sendMessageCommandHelp("Change weather in world", "weather <clear/rain/sun> " + "[world]");
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

    public void sun(Sender sender, Argument arg) {
        if (!sender.hasPermission("exbukkit.weather.sun", 925)) {
            return;
        }

        if (arg != null) {
            if (arg.isWorldName(true)) {
                ExWorld world = arg.toWorld();
                world.setStorm(false);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Changed weather to " + ChatColor.VALUE + "sun " + ChatColor.PERSONAL + "in world " + ChatColor.VALUE + world.getName());
            }
        } else if (sender.isPlayer(false)) {
            World world = sender.getPlayer().getWorld();
            world.setStorm(false);
            sender.sendPluginMessage(ChatColor.PERSONAL + "Changed weather to " + ChatColor.VALUE + "sun " + ChatColor.PERSONAL + "in world " + ChatColor.VALUE + world.getName());
        } else {
            sender.sendMessageCommandHelp("Set sun in world", "sun <world>");
        }
    }

    public void rain(Sender sender, Argument arg) {
        if (!sender.hasPermission("exbukkit.weather.rain", 926)) {
            return;
        }

        if (arg != null) {
            if (arg.isWorldName(true)) {
                ExWorld world = arg.toWorld();
                world.setStorm(true);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Changed weather to " + ChatColor.VALUE + "rain " + ChatColor.PERSONAL + "in world " + ChatColor.VALUE + world.getName());
            }
        } else if (sender.isPlayer(false)) {
            World world = sender.getPlayer().getWorld();
            world.setStorm(true);
            sender.sendPluginMessage(ChatColor.PERSONAL + "Changed weather to " + ChatColor.VALUE + "rain " + ChatColor.PERSONAL + "in world " + ChatColor.VALUE + world.getName());
        } else {
            sender.sendMessageCommandHelp("Set rain in world", "rain <world>");
        }
    }

}
