/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserQuitEvent;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import java.util.HashMap;
import java.util.Stack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Teleport implements Listener {

    public static void teleport(Sender sender, Arguments<Argument> args) {
        if (args.isLengthEquals(1, false)) {
            if (!sender.hasPermission(teleportToOtherPerm)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            User user = sender.getUser();
            User to = args.get(0).toUser();
            user.teleport(to);
            sender.sendPluginMessage(Component.text("Teleported to ", ExTextColor.PERSONAL)
                    .append(to.getChatNameComponent()));

        } else if (args.isLengthEquals(2, false)) {

            if (!((sender.hasPermission(teleportToOtherPerm.getPermission()) && sender.isPlayer(
                    false)
                    && sender.getPlayer().getName().equals(args.getString(0)))
                    || sender.hasPermission(teleportToOtherOtherPerm))) {
                return;
            }

            if (!(args.get(0).isPlayerName(true) && args.get(1).isPlayerName(true))) {
                return;
            }

            User user1 = args.get(0).toUser();
            User user2 = args.get(1).toUser();
            user1.teleport(user2);
            user1.sendPluginMessage(Plugin.BUKKIT,
                    Component.text("Teleported to ", ExTextColor.PERSONAL)
                            .append(user2.getChatNameComponent()));
            if (!sender.getName().equals(user1.getName()) && !sender.getName()
                    .equals(user2.getName())) {
                sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                        .append(user1.getChatNameComponent())
                        .append(Component.text(" to ", ExTextColor.PERSONAL))
                        .append(user2.getChatNameComponent()));
            }
        } else if (args.isLengthEquals(3, false)) {
            if (!sender.hasPermission(teleportLocationPerm)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }

            User senderUser = sender.getUser();

            Integer x;
            Integer y;
            Integer z;

            if (args.getString(0).equals("~")) {
                x = senderUser.getLocation().getBlockX();
            } else if (args.get(0).isInt(true)) {
                x = args.get(0).toInt();
            } else {
                return;
            }

            if (args.getString(1).equals("~")) {
                y = senderUser.getLocation().getBlockY();
            } else if (args.get(1).isInt(true)) {
                y = args.get(1).toInt();
            } else {
                return;
            }

            if (args.getString(2).equals("~")) {
                z = senderUser.getLocation().getBlockZ();
            } else if (args.get(2).isInt(true)) {
                z = args.get(2).toInt();
            } else {
                return;
            }

            Location loc = new Location(senderUser.getWorld(), x, y, z);
            senderUser.getWorld().loadChunk(loc.getChunk());
            senderUser.teleport(loc);
            sender.sendPluginMessage(Component.text("Teleported to ", ExTextColor.PERSONAL)
                    .append(Component.text(x + " " + y + " " + z, ExTextColor.VALUE)));

        } else if (args.isLengthEquals(4, false)) {
            if (!sender.hasPermission(teleportLocationOtherPerm)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            User user = args.get(0).toUser();

            Integer x;
            Integer y;
            Integer z;

            if (sender.isPlayer(false) && args.getString(1).equals("~")) {
                x = sender.getPlayer().getLocation().getBlockX();
            } else if (args.get(1).isInt(true)) {
                x = args.get(1).toInt();
            } else {
                return;
            }

            if (sender.isPlayer(false) && args.getString(2).equals("~")) {
                y = sender.getPlayer().getLocation().getBlockY();
            } else if (args.get(2).isInt(true)) {
                y = args.get(2).toInt();
            } else {
                return;
            }

            if (sender.isPlayer(false) && args.getString(3).equals("~")) {
                z = sender.getPlayer().getLocation().getBlockZ();
            } else if (args.get(3).isInt(true)) {
                z = args.get(3).toInt();
            } else {
                return;
            }

            Location loc = new Location(user.getWorld(), x, y, z);
            user.getWorld().loadChunk(loc.getChunk());
            user.teleport(loc);

            user.sendPluginMessage(Plugin.BUKKIT,
                    Component.text("Teleported to ", ExTextColor.PERSONAL)
                            .append(Component.text(x + " " + y + " " + z, ExTextColor.VALUE)));
            if (!sender.getName().equals(Server.getUser(user).getName())) {
                sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent())
                        .append(Component.text(" to ", ExTextColor.PERSONAL))
                        .append(Component.text(x + " " + y + " " + z, ExTextColor.VALUE)));
            }
        } else {
            sender.sendMessageTooFewManyArguments();
            sender.sendTDMessageCommandHelp("Teleport to player", "tp <player>");
            sender.sendTDMessageCommandHelp("Teleport player to player", "tp <player> <player>");
            sender.sendTDMessageCommandHelp("Teleport to location", "tp <x> <y> <z>");
            sender.sendTDMessageCommandHelp("Teleport player to location",
                    "tp <player> <x> <y> <z>");
        }

    }

    public static void teleportHere(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendTDMessageCommandHelp("Teleport player here", "tph <player>");
            return;
        }

        if (!sender.hasPermission(teleportHerePerm)) {
            return;
        }

        if (!(args.get(0).isPlayerName(true) && sender.isPlayer(true))) {
            return;
        }

        User user = sender.getUser();
        User other = args.get(0).toUser();
        other.teleport(user);
        other.sendPluginMessage(Plugin.BUKKIT,
                Component.text("Teleported to ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
        sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                .append(other.getChatNameComponent()));
    }

    public static void teleportWorld(Sender sender, Arguments<Argument> args) {
        if (args.isLengthHigherEquals(1, true)) {
            if (args.get(0).isPlayerName(false)) {
                if (!sender.hasPermission(teleportWorldOtherPerm)) {
                    return;
                }

                if (!args.isLengthHigherEquals(2, true)) {
                    return;
                }

                if (!args.get(1).isWorldName(true)) {
                    return;
                }

                ExWorld world = args.get(1).toWorld();
                User user = args.get(0).toUser();

                if (args.isLengthEquals(2, false)) {
                    user.teleport(world);

                    user.sendPluginMessage(Plugin.BUKKIT,
                            Component.text("Teleported to ", ExTextColor.PERSONAL)
                                    .append(Component.text(world.getName(), ExTextColor.VALUE))
                                    .append(Component.text(" spawn", ExTextColor.PERSONAL)));
                    if (!sender.getName().equals(user.getName())) {
                        sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                                .append(user.getChatNameComponent())
                                .append(Component.text(" to ", ExTextColor.PERSONAL))
                                .append(Component.text(world.getName(), ExTextColor.VALUE))
                                .append(Component.text(" spawn", ExTextColor.PERSONAL)));
                    }
                } else if (args.isLengthEquals(5, false)) {
                    if (!(args.get(2).isInt(true) && args.get(3).isInt(true) && args.get(4)
                            .isInt(true))) {
                        return;
                    }

                    int x = args.get(2).toInt();
                    int y = args.get(3).toInt();
                    int z = args.get(4).toInt();

                    Location loc = new ExLocation(world, x, y, z);
                    world.loadChunk(loc.getChunk());
                    user.teleport(loc);

                    user.sendPluginMessage(Plugin.BUKKIT,
                            Component.text("Teleported to ", ExTextColor.PERSONAL)
                                    .append(Component.text(world.getName(), ExTextColor.VALUE))
                                    .append(Component.text(x + " " + y + " " + z,
                                            ExTextColor.VALUE)));
                    if (!sender.getName().equals(user.getName())) {
                        sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                                .append(user.getChatNameComponent())
                                .append(Component.text(" to ", ExTextColor.PERSONAL))
                                .append(Component.text(world.getName(), ExTextColor.VALUE))
                                .append(Component.text(x + " " + y + " " + z)
                                        .color(ExTextColor.VALUE)));
                    }
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendTDMessageCommandHelp("Teleport to world",
                            "tpw <world> [x] " + "[y] [z]");
                    sender.sendTDMessageCommandHelp("Teleport player to world",
                            "tpw " + "<player> <world> [x] [y] [z]");
                }
            } else if (args.get(0).isWorldName(false)) {

                if (!sender.hasPermission(teleportWorldPerm)) {
                    return;
                }

                if (!sender.isPlayer(true)) {
                    return;
                }

                ExWorld world = args.get(0).toWorld();

                if (args.isLengthEquals(1, false)) {
                    sender.getUser().teleport(world);

                    sender.sendPluginMessage(
                            Component.text("Teleported to ").color(ExTextColor.PERSONAL)
                                    .append(Component.text(world.getName())
                                            .color(ExTextColor.VALUE))
                                    .append(Component.text(" spawn").color(ExTextColor.PERSONAL)));
                } else if (args.isLengthEquals(4, false)) {
                    if (!(args.get(1).isInt(true) && args.get(2).isInt(true)
                            && args.get(3).isInt(true))) {
                        return;
                    }

                    int x = args.get(1).toInt();
                    int y = args.get(2).toInt();
                    int z = args.get(3).toInt();

                    Location loc = new ExLocation(world, x, y, z);
                    world.loadChunk(loc.getChunk());
                    sender.getUser().teleport(loc);

                    sender.sendPluginMessage(
                            Component.text("Teleported to ").color(ExTextColor.PERSONAL)
                                    .append(Component.text(world.getName())
                                            .color(ExTextColor.VALUE))
                                    .append(Component.text(x + " " + y + " " + z)
                                            .color(ExTextColor.VALUE)));
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
                    sender.sendTDMessageCommandHelp("Teleport player to world",
                            "tpw <player> " + "<world> [x] [y] [z]");
                }
            } else {
                sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
                sender.sendTDMessageCommandHelp("Teleport player to world",
                        "tpw <player> <world> [x] " + "[y] [z]");
            }
        } else {
            sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
            sender.sendTDMessageCommandHelp("Teleport player to world",
                    "tpw <player> <world> [x] [y] " + "[z]");
        }
    }

    public static void teleportSpawn(Sender sender) {
        if (sender.hasPermission(teleportSpawnPerm)) {
            if (sender.isPlayer(true)) {
                sender.getPlayer().teleport(sender.getPlayer().getWorld().getSpawnLocation());
            }
        }
    }

    public static void teleportAsk(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendTDMessageCommandHelp("Ask a teleport", "tpa <player>");
            return;
        }

        if (!(sender.hasPermission(teleportAskPerm) && sender.isPlayer(true))) {
            return;
        }

        if (!args.get(0).isPlayerName(true)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = args.get(0).toUser();

        Stack<User> users = Teleport.ask.get(user);

        if (users != null) {
            if (users.contains(sender.getUser())) {
                sender.sendPluginMessage(
                        Component.text("You have already send a teleport request to ")
                                .color(ExTextColor.WARNING)
                                .append(user.getChatNameComponent()));
                return;
            }
        } else {
            users = new Stack<>();
        }

        users.push(sender.getUser());
        Teleport.ask.put(user, users);

        //user msg
        user.sendPluginMessage(Plugin.BUKKIT, sender.getChatName().color(ExTextColor.VALUE)
                .append(Component.text(" requests a teleport").color(ExTextColor.PERSONAL)));

        user.sendClickablePluginMessage(Plugin.BUKKIT,
                Component.text("Use ", ExTextColor.PERSONAL)
                        .append(Component.text("/tpaccept ", ExTextColor.VALUE,
                                TextDecoration.UNDERLINED))
                        .append(sender.getUser().getChatNameComponent())
                        .append(Component.text(" to accept the teleport request",
                                ExTextColor.PERSONAL)),
                "/tpaccept ",
                Component.text("Click to accept the teleport request"),
                net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

        user.sendClickablePluginMessage(Plugin.BUKKIT, Component.text("Use ", ExTextColor.PERSONAL)
                        .append(Component.text("/tpdeny ", ExTextColor.VALUE, TextDecoration.UNDERLINED))
                        .append(sender.getUser().getChatNameComponent())
                        .append(Component.text(" to deny the teleport request", ExTextColor.PERSONAL)),
                "/tpdeny ",
                Component.text("Click to deny the teleport request"),
                net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

        sender.sendPluginMessage(
                Component.text("Send teleport request to ").color(ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
    }

    public static void teleportHereAsk(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendTDMessageCommandHelp("Ask a teleporthere", "tpha <player>");
            return;
        }

        if (!(sender.hasPermission(teleportAskPerm) && sender.isPlayer(true))) {
            return;
        }

        if (!args.get(0).isPlayerName(true)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = args.get(0).toUser();

        Stack<User> users = Teleport.askHere.get(user);

        if (Teleport.askHere.containsKey(user)) {
            if (users.contains(sender.getUser())) {
                sender.sendPluginMessage(
                        Component.text("You have already send a teleport here request to ",
                                        ExTextColor.WARNING)
                                .append(user.getChatNameComponent()));
                return;
            }
        } else {
            users = new Stack<>();
        }

        users.push(sender.getUser());
        Teleport.askHere.put(user, users);

        //user msg
        user.sendPluginMessage(Plugin.BUKKIT, sender.getChatName().color(ExTextColor.VALUE)
                .append(Component.text(" requests a teleport-here").color(ExTextColor.PERSONAL)));

        user.sendClickablePluginMessage(Plugin.BUKKIT,
                Component.text("Use ", ExTextColor.PERSONAL)
                        .append(Component.text("/tpaccept ", ExTextColor.VALUE,
                                TextDecoration.UNDERLINED))
                        .append(sender.getUser().getChatNameComponent())
                        .append(Component.text(" to accept the teleport-here request",
                                ExTextColor.PERSONAL)),
                "/tpaccept ",
                Component.text("Click to accept the teleport-here request"),
                net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

        user.sendClickablePluginMessage(Plugin.BUKKIT, Component.text("Use ", ExTextColor.PERSONAL)
                        .append(Component.text("/tpdeny ", ExTextColor.VALUE, TextDecoration.UNDERLINED))
                        .append(sender.getUser().getChatNameComponent())
                        .append(Component.text(" to deny the teleport-here request", ExTextColor.PERSONAL)),
                "/tpdeny ",
                Component.text("Click to deny the teleport-her request"),
                net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

        sender.sendPluginMessage(
                Component.text("Send teleport-here request to ").color(ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
    }

    public static void accept(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission(teleportAskAcceptPerm)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();
        if (Teleport.ask.containsKey(user)) {
            Stack<User> users = Teleport.ask.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(
                        Component.text("You haven't open requests", ExTextColor.WARNING));
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                teleportUserToUser(user, enquirer);
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(
                        Component.text("You haven't an open request by ", ExTextColor.WARNING)
                                .append(argUser.getChatNameComponent()));
                return;
            }

            users.pop();
            teleportUserToUser(user, argUser);
        } else if (Teleport.askHere.containsKey(user)) {
            Stack<User> users = Teleport.askHere.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(
                        Component.text("You haven't open requests", ExTextColor.WARNING));
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                teleportUserToUser(enquirer, user);
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(
                        Component.text("You haven't an open request by ", ExTextColor.WARNING)
                                .append(argUser.getChatNameComponent()));
                return;
            }

            users.pop();
            teleportUserToUser(argUser, user);

        } else {
            sender.sendPluginMessage(
                    Component.text("You haven't open requests", ExTextColor.WARNING));
        }
    }

    private static void teleportUserToUser(User user, User teleporter) {
        teleporter.teleport(user);

        user.sendPluginMessage(Plugin.BUKKIT,
                Component.text("Teleported ").color(ExTextColor.PERSONAL)
                        .append(teleporter.getChatNameComponent()));
        teleporter.sendPluginMessage(Plugin.BUKKIT,
                Component.text("Teleported to ").color(ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent()));
    }

    public static void deny(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission(teleportAskDenyPerm)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();
        if (Teleport.ask.containsKey(user)) {
            Stack<User> users = Teleport.ask.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(
                        Component.text("You haven't open requests").color(ExTextColor.WARNING));
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                sender.sendPluginMessage(
                        Component.text("Denied teleport request by ").color(ExTextColor.PERSONAL)
                                .append(enquirer.getChatNameComponent()));
                enquirer.sendPluginMessage(Plugin.BUKKIT, user.getChatNameComponent()
                        .append(Component.text(" denied your teleport request")
                                .color(ExTextColor.PERSONAL)));
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(
                        Component.text("You haven't an open request by ").color(ExTextColor.WARNING)
                                .append(argUser.getChatNameComponent()));
                return;
            }

            users.pop();
            sender.sendPluginMessage(
                    Component.text("Denied teleport request by ").color(ExTextColor.PERSONAL)
                            .append(argUser.getChatNameComponent()));
            argUser.sendPluginMessage(Plugin.BUKKIT, user.getChatNameComponent()
                    .append(Component.text(" denied your teleport request")
                            .color(ExTextColor.PERSONAL)));

        } else if (Teleport.askHere.containsKey(user)) {
            Stack<User> users = Teleport.askHere.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(
                        Component.text("You haven't open requests").color(ExTextColor.WARNING));
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                sender.sendPluginMessage(Component.text("Denied teleport-here request by ")
                        .color(ExTextColor.PERSONAL)
                        .append(enquirer.getChatNameComponent()));
                enquirer.sendPluginMessage(Plugin.BUKKIT, user.getChatNameComponent()
                        .append(Component.text(" denied your teleport-here request")
                                .color(ExTextColor.PERSONAL)));
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(
                        Component.text("You haven't an open request by ").color(ExTextColor.WARNING)
                                .append(argUser.getChatNameComponent()));
                return;
            }

            users.pop();
            sender.sendPluginMessage(
                    Component.text("Denied teleport-here request by ").color(ExTextColor.PERSONAL)
                            .append(argUser.getChatNameComponent()));
            argUser.sendPluginMessage(Plugin.BUKKIT, user.getChatNameComponent()
                    .append(Component.text(" denied your teleport-here request")
                            .color(ExTextColor.PERSONAL)));

        } else {
            sender.sendPluginMessage(
                    Component.text("You haven't open requests").color(ExTextColor.WARNING));
        }
    }

    public static void setSpawn(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission(teleportSetSpawnPerm)) {
            return;
        }

        if (sender.isPlayer(true)) {
            Player p = sender.getPlayer();
            if (args.isLengthEquals(3, false)) {
                if (!(args.get(0).isInt(true) && args.get(1).isInt(true) && args.get(2)
                        .isInt(true))) {
                    sender.sendTDMessageCommandHelp("Set world spawn", "setspawn");
                    sender.sendTDMessageCommandHelp("Set world spawn", "setspawn <x> <y> <z>");
                }

                p.getWorld().setSpawnLocation(
                        new Location(p.getWorld(), args.get(0).toInt(), args.get(1).toInt(),
                                args.get(2).toInt()));
                sender.sendPluginMessage(Component.text("Set spawn in ", ExTextColor.PERSONAL)
                        .append(Component.text(p.getWorld().getName(), ExTextColor.VALUE))
                        .append(Component.text(" to: ", ExTextColor.PERSONAL))
                        .append(Component.text(
                                args.get(0).getString() + " " + args.get(1).getString() + " "
                                        + args.get(2).getString(), ExTextColor.VALUE)));

            } else if (args.isLengthEquals(0, false)) {
                p.getWorld().setSpawnLocation(p.getLocation());
                sender.sendMessage(Component.text("Set spawn to: ", ExTextColor.PERSONAL)
                        .append(Component.text(
                                p.getLocation().getBlockX() + " " + p.getLocation().getBlockY()
                                        + " " + p.getLocation().getBlockZ(), ExTextColor.VALUE)));
            } else {
                sender.sendMessageTooFewManyArguments();
                sender.sendTDMessageCommandHelp("Set world spawn", "setspawn");
                sender.sendTDMessageCommandHelp("Set world spawn", "setspawn <x> <y> <z>");
            }

        } else if (sender instanceof ConsoleCommandSender) {
            if (!args.isLengthEquals(4, true)) {
                sender.sendTDMessageCommandHelp("Set world spawn", "setspawn <world> <x> <y> <z>");
                return;
            }

            if (!args.get(0).isWorldName(true)) {
                sender.sendTDMessageCommandHelp("Set world spawn", "setspawn <world> <x> <y> <z>");
                return;
            }

            ExWorld world = args.get(0).toWorld();
            if (!(args.get(1).isInt(true) && args.get(2).isInt(true) && args.get(3).isInt(true))) {
                sender.sendTDMessageCommandHelp("Set world spawn",
                        "setspawn <world> <x> " + "<y> <z>");
                return;
            }

            world.setSpawnLocation(new ExLocation(world, args.get(1).toInt(), args.get(2).toInt(),
                    args.get(3).toInt()));
            sender.sendPluginMessage(Component.text("Set spawn in ", ExTextColor.PERSONAL)
                    .append(Component.text(world.getName(), ExTextColor.VALUE))
                    .append(Component.text("to: ", ExTextColor.PERSONAL))
                    .append(Component.text(
                            args.get(1).getString() + " " + args.get(2).getString() + " "
                                    + args.get(3).getString(), ExTextColor.VALUE)));
        }

    }

    public static void back(Sender sender, Arguments<Argument> args) {
        if (args.isLengthEquals(0, false) && sender.isPlayer(true)) {
            if (sender.hasPermission(teleportBackPerm)) {
                User user = sender.getUser();
                user.teleport(user.getLastLocation());
                sender.sendPluginMessage(
                        Component.text("Teleported to last location", ExTextColor.PERSONAL));
            }
        } else if (args.isLengthEquals(1, true) && args.get(0).isPlayerName(true)) {
            if (sender.hasPermission(teleportBackOtherPerm)) {
                User user = args.get(0).toUser();
                user.teleport(user.getLastLocation());
                sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent())
                        .append(Component.text(" to last location", ExTextColor.PERSONAL)));
                user.sendPluginMessage(Plugin.BUKKIT,
                        Component.text("Teleported to last location", ExTextColor.PERSONAL));
            }
        }
    }

    public static void teleportHereAll(Sender sender) {
        if (!sender.hasPermission("exbukkit.teleport.all.here")) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User senderUser = sender.getUser();

        for (User user : Server.getUsers()) {
            if (!user.equals(senderUser)) {
                user.teleport(senderUser);
                user.sendPluginMessage(Plugin.BUKKIT,
                        Component.text("Teleported to ", ExTextColor.PERSONAL)
                                .append(senderUser.getChatNameComponent()));
            }
        }

        sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                .append(Component.text("all", ExTextColor.VALUE)));
    }

    public static void loadCodes(Plugin plugin) {
        teleportToOtherPerm = plugin.createPermssionCode("exbukkit.teleport.toother");
        teleportToOtherOtherPerm = plugin.createPermssionCode("exbukkit.teleport.toother.other");
        teleportLocationPerm = plugin.createPermssionCode("exbukkit.teleport.location");
        teleportLocationOtherPerm = plugin.createPermssionCode("exbukkit.teleport.location.other");
        teleportHerePerm = plugin.createPermssionCode("exbukkit.teleport.here");
        teleportWorldPerm = plugin.createPermssionCode("exbukkit.teleport.world");
        teleportWorldOtherPerm = plugin.createPermssionCode("exbukkit.teleport.world.other");
        teleportSpawnPerm = plugin.createPermssionCode("exbukkit.teleport.spawn");
        teleportSetSpawnPerm = plugin.createPermssionCode("exbukkit.teleport.setspawn");
        teleportAskPerm = plugin.createPermssionCode("exbukkit.teleport.ask");
        teleportAskAcceptPerm = plugin.createPermssionCode("exbukkit.teleport.ask.accept");
        teleportAskDenyPerm = plugin.createPermssionCode("exbukkit.teleport.ask.deny");
        teleportBackPerm = plugin.createPermssionCode("exbukkit.teleport.back");
        teleportBackOtherPerm = plugin.createPermssionCode("exbukkit.teleport.back.other");
    }

    private static final HashMap<User, Stack<User>> ask = new HashMap<>();
    private static final HashMap<User, Stack<User>> askHere = new HashMap<>();

    private static Code teleportToOtherPerm;
    private static Code teleportToOtherOtherPerm;
    private static Code teleportLocationPerm;
    private static Code teleportLocationOtherPerm;
    private static Code teleportHerePerm;
    private static Code teleportWorldPerm;
    private static Code teleportWorldOtherPerm;
    private static Code teleportSpawnPerm;
    private static Code teleportSetSpawnPerm;
    private static Code teleportAskPerm;
    private static Code teleportAskAcceptPerm;
    private static Code teleportAskDenyPerm;
    private static Code teleportBackPerm;
    private static Code teleportBackOtherPerm;

    @EventHandler
    public void onUserQuit(UserQuitEvent e) {
        User user = e.getUser();
        Teleport.ask.remove(user);
        Teleport.askHere.remove(user);

        for (Stack<User> users : ask.values()) {
            users.remove(user);
        }

        for (Stack<User> users : askHere.values()) {
            users.remove(user);
        }
    }

}
