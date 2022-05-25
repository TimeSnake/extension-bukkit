package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import org.bukkit.World;

import java.util.List;

public class CmdTime implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        switch (cmd.getName()) {

            case "time":
                this.handleCmdTime(sender, args);
                break;
            case "day":
                if (sender.hasPermission("exbukkit.time.day", 922)) {
                    if (args.isLengthEquals(1, false)) {
                        this.setDay(sender, args.get(0));
                    } else if (sender.isPlayer(false)) {
                        this.setDay(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                    } else sender.sendMessageCommandHelp("Set day in world", "day <world>");
                }
                break;
            case "night":
                if (sender.hasPermission("exbukkit.time.night", 923)) {
                    if (args.isLengthEquals(1, false)) {
                        this.setNight(sender, args.get(0));
                    } else if (sender.isPlayer(false)) {
                        this.setNight(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                    } else sender.sendMessageCommandHelp("Set night in world", "night <world>");
                }

                break;
            case "noon":
                if (sender.hasPermission("exbukkit.time.noon", 924)) {
                    if (args.isLengthEquals(1, false)) {
                        this.setNoon(sender, args.get(0));
                    } else if (sender.isPlayer(false)) {
                        this.setNoon(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                    } else sender.sendMessageCommandHelp("Set noon in world", "noon <world>");
                }
                break;

        }
    }

    public void handleCmdTime(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            if (sender.isPlayer(false)) {
                long ticks = sender.getPlayer().getWorld().getTime();
                sender.sendPluginMessage(ChatColor.PERSONAL + "Current time: " + ChatColor.VALUE + ticks + ChatColor.PERSONAL + " ticks, " + ChatColor.VALUE + new Argument(sender, String.valueOf(ticks)).toTime());
                return;
            } else {
                sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
            }
            return;
        }

        switch (args.getString(0).toLowerCase()) {
            case "set":
                if (!args.isLengthHigherEquals(2, true)) {
                    sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
                    return;
                }

                if (!sender.hasPermission("exbukkit.time.set", 921)) {
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
                        this.set(sender, args.get(2), new Argument(sender, String.valueOf(args.get(1).toTicks())));
                    }
                } else if (sender.isPlayer(false)) {

                    World world = sender.getPlayer().getWorld();

                    switch (args.getString(1).toLowerCase()) {
                        case "day":
                            this.setDay(sender, new Argument(sender, world.getName()));
                            return;
                        case "night":
                            this.setNight(sender, new Argument(sender, world.getName()));
                            return;
                        case "noon":
                            this.setNoon(sender, new Argument(sender, world.getName()));
                            return;
                        default:
                            if (args.get(1).isInt(false)) {
                                this.set(sender, new Argument(sender, world.getName()), args.get(1));
                            } else if (args.get(1).isTime(true)) {
                                this.set(sender, new Argument(sender, world.getName()), new Argument(sender,
                                        String.valueOf(args.get(1).toTicks())));
                            }
                    }
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
                }
                break;
            case "day":
                if (!sender.hasPermission("exbukkit.time.day", 922)) {
                    return;
                }

                if (args.isLengthHigherEquals(3, false)) {
                    this.setDay(sender, args.get(2));
                } else if (sender.isPlayer(false)) {
                    this.setDay(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
                }
                break;
            case "night":
                if (!sender.hasPermission("exbukkit.time.night", 923)) {
                    return;
                }

                if (args.isLengthHigherEquals(3, false)) {
                    this.setNight(sender, args.get(2));
                } else if (sender.isPlayer(false)) {
                    this.setNight(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
                }
                break;
            case "noon":
                if (!sender.hasPermission("exbukkit.time.noon", 924)) {
                    return;
                }

                if (args.isLengthHigherEquals(3, false)) {
                    this.setNoon(sender, args.get(2));
                } else if (sender.isPlayer(false)) {
                    this.setNoon(sender, new Argument(sender, sender.getPlayer().getWorld().getName()));
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
                }
                break;
            default:
                sender.sendMessageCommandHelp("Set time in world", "time set <time> [world]");
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (cmd.getName().equalsIgnoreCase("time")) {
            if (args.getLength() == 1) {
                return List.of("set", "day", "night", "noon");
            } else if (args.get(0).equalsIgnoreCase("set")) {
                if (args.getLength() == 2) {
                    return List.of("day", "night", "noon");
                } else if (args.getLength() == 3) {
                    return Server.getCommandManager().getTabCompleter().getWorldNames();
                }
            }
        }
        return null;
    }

    public void set(Sender sender, Argument arg0, Argument arg1) {
        if (sender.hasPermission("exbukkit.time.set", 921)) {
            if (arg0.isWorldName(true) && arg1.isInt(true)) {
                ExWorld world = arg0.toWorld();
                world.setTime(arg1.toInt());
                sender.sendPluginMessage(ChatColor.PERSONAL + "Updated time in world " + ChatColor.VALUE + world.getName() + " " + ChatColor.PERSONAL + "to " + ChatColor.VALUE + arg1.toInt() + " " + ChatColor.PERSONAL + "ticks, " + ChatColor.VALUE + arg1.toTime());
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

    public int getCurrentTime(Sender sender, Argument arg) {
        if (arg.isWorldName(true)) {
            return (int) arg.toWorld().getTime();
        }
        sender.sendMessageWorldNotExist(arg.getString());
        return 0;
    }

}
