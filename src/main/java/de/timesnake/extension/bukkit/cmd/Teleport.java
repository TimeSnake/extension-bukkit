package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserQuitEvent;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.cmd.Arguments;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Stack;

public class Teleport implements Listener {

    private static final String senderPlugin = Server.getChat().getSenderPlugin(Plugin.BUKKIT);

    private static final HashMap<User, Stack<User>> ask = new HashMap<>();
    private static final HashMap<User, Stack<User>> askHere = new HashMap<>();

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


    public static void teleport(Sender sender, Arguments<Argument> args) {
        if (args.isLengthEquals(1, false)) {
            if (!sender.hasPermission("exbukkit.teleport.toother", 901)) {
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
            sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + to.getChatName());

        } else if (args.isLengthEquals(2, false)) {

            if (!((sender.hasPermission("exbukkit.teleport.toother") && sender.isPlayer(false) && sender.getPlayer().getName().equals(args.getString(0))) || sender.hasPermission("exbukkit.teleport.toother.other", 902))) {
                return;
            }

            if (!(args.get(0).isPlayerName(true) && args.get(1).isPlayerName(true))) {
                return;
            }

            Player p0 = args.get(0).toPlayer();
            Player p1 = args.get(1).toPlayer();
            p0.teleport(p1);
            p0.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + " " + Server.getUser(p1).getChatName());
            if (!sender.getName().equals(p0.getName()) && !sender.getName().equals(p1.getName())) {
                sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + Server.getUser(p0).getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + Server.getUser(p1).getChatName());
            }
        } else if (args.isLengthEquals(3, false)) {
            if (!sender.hasPermission("exbukkit.teleport.location", 904)) {
                return;
            }

            if (!sender.isPlayer(true)) {
                return;
            }


            Player p = sender.getPlayer();

            Integer x;
            Integer y;
            Integer z;

            if (args.getString(0).equals("~")) {
                x = p.getLocation().getBlockX();
            } else if (args.get(0).isInt(true)) {
                x = args.get(0).toInt();
            } else {
                return;
            }

            if (args.getString(1).equals("~")) {
                y = p.getLocation().getBlockY();
            } else if (args.get(1).isInt(true)) {
                y = args.get(1).toInt();
            } else {
                return;
            }

            if (args.getString(2).equals("~")) {
                z = p.getLocation().getBlockZ();
            } else if (args.get(2).isInt(true)) {
                z = args.get(2).toInt();
            } else {
                return;
            }

            Location loc = new Location(p.getWorld(), x, y, z);
            p.getWorld().loadChunk(loc.getChunk());
            p.teleport(loc);
            sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + x + " " + y + " " + z);

        } else if (args.isLengthEquals(4, false)) {
            if (!sender.hasPermission("exbukkit.teleport.location.other", 905)) {
                return;
            }

            if (!args.get(0).isPlayerName(true)) {
                return;
            }

            Player p = Bukkit.getPlayer(args.get(0).getString());

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

            Location loc = new Location(p.getWorld(), x, y, z);
            p.getWorld().loadChunk(loc.getChunk());
            p.teleport(loc);

            p.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + x + " " + y + " " + z);
            if (!sender.getName().equals(Server.getUser(p).getChatName())) {
                sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + Server.getUser(p).getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + x + " " + y + " " + z);
            }
        } else {
            sender.sendMessageTooFewManyArguments();
            sender.sendMessageCommandHelp("Teleport to player", "tp <player>");
            sender.sendMessageCommandHelp("Teleport player to player", "tp <player> <player>");
            sender.sendMessageCommandHelp("Teleport to location", "tp <x> <y> <z>");
            sender.sendMessageCommandHelp("Teleport player to location", "tp <player> <x> <y> <z>");
        }

    }

    public static void teleportHere(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendMessageCommandHelp("Teleport player here", "tph <player>");
            return;
        }

        if (!sender.hasPermission("exbukkit.teleport.here", 903)) {
            return;
        }

        if (!(args.get(0).isPlayerName(true) && sender.isPlayer(true))) {
            return;
        }

        User user = sender.getUser();
        User other = args.get(0).toUser();
        other.teleport(user);
        other.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + user.getChatName());
        sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + other.getChatName());
    }

    public static void teleportWorld(Sender sender, Arguments<Argument> args) {
        if (args.isLengthHigherEquals(1, true)) {
            if (args.get(0).isPlayerName(false)) {
                if (!sender.hasPermission("exbukkit.teleport.world.other", 944)) {
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

                    user.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + args.get(1).getString() + " spawn");
                    if (!sender.getName().equals(user.getChatName())) {
                        sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + args.get(1).getString() + " spawn");
                    }
                } else if (args.isLengthEquals(5, false)) {
                    if (!(args.get(2).isInt(true) && args.get(3).isInt(true) && args.get(4).isInt(true))) {
                        return;
                    }

                    Location loc = new ExLocation(world, args.get(2).toInt(), args.get(3).toInt(), args.get(4).toInt());
                    world.loadChunk(loc.getChunk());
                    user.teleport(loc);

                    user.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + args.get(1).getString() + " " + args.get(2).getString() + " " + args.get(3).getString() + " " + args.get(4).getString());
                    if (!sender.getName().equals(user.getChatName())) {
                        sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + args.get(1).getString() + " " + args.get(2).getString() + " " + args.get(3).getString() + " " + args.get(4).getString());
                    }
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Teleport to world", "tpw <world> [x] " + "[y] [z]");
                    sender.sendMessageCommandHelp("Teleport player to world", "tpw " + "<player> <world> [x] [y] [z]");
                }
            } else if (args.get(0).isWorldName(false)) {

                if (!sender.hasPermission("exbukkit.teleport.world", 943)) {
                    return;
                }

                if (!sender.isPlayer(true)) {
                    return;
                }

                ExWorld world = args.get(0).toWorld();

                if (args.isLengthEquals(1, false)) {
                    sender.getUser().teleport(world);

                    sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + args.get(0).getString() + " spawn");
                } else if (args.isLengthEquals(4, false)) {
                    if (!(args.get(1).isInt(true) && args.get(2).isInt(true) && args.get(3).isInt(true))) {
                        return;
                    }

                    Location loc = new ExLocation(world, args.get(1).toInt(), args.get(2).toInt(), args.get(3).toInt());
                    world.loadChunk(loc.getChunk());
                    sender.getUser().teleport(loc);

                    sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + args.get(0).getString() + " " + args.get(1).getString() + " " + args.get(2).getString() + " " + args.get(3).getString());
                } else {
                    sender.sendMessageTooFewManyArguments();
                    sender.sendMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
                    sender.sendMessageCommandHelp("Teleport player to world", "tpw <player> " + "<world> [x] [y] [z]");
                }
            } else {
                sender.sendMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
                sender.sendMessageCommandHelp("Teleport player to world", "tpw <player> <world> [x] " + "[y] [z]");
            }
        } else {
            sender.sendMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
            sender.sendMessageCommandHelp("Teleport player to world", "tpw <player> <world> [x] [y] " + "[z]");
        }
    }

    public static void teleportSpawn(Sender sender) {
        if (sender.hasPermission("exbukkit.teleport.spawn", 906)) {
            if (sender.isPlayer(true)) {
                sender.getPlayer().teleport(sender.getPlayer().getWorld().getSpawnLocation());
            }
        }
    }

    public static void teleportAsk(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendMessageCommandHelp("Ask a teleport", "tpa <player>");
            return;
        }

        if (!(sender.hasPermission("exbukkit.teleport.ask", 907) && sender.isPlayer(true))) {
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
                sender.sendPluginMessage(ChatColor.WARNING + "You have already send a teleport request to " + user.getChatName());
                return;
            }
        } else {
            users = new Stack<>();
        }

        users.push(sender.getUser());
        Teleport.ask.put(user, users);

        //user msg
        user.sendPluginMessage(Plugin.BUKKIT, ChatColor.VALUE + sender.getChatName() + ChatColor.PERSONAL + " requests a teleport");

        TextComponent tc0 = new TextComponent();
        tc0.setText(senderPlugin + ChatColor.PERSONAL + "Use " + ChatColor.VALUE + "/tpaccept " + sender.getUser().getChatName() + ChatColor.PERSONAL + " to accept the teleport");
        tc0.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.PERSONAL + "Click to accept the teleport")));
        tc0.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getUser().getName()));

        TextComponent tc1 = new TextComponent();
        tc1.setText(senderPlugin + ChatColor.PERSONAL + "Use " + ChatColor.VALUE + "/tpdeny " + sender.getUser().getChatName() + ChatColor.PERSONAL + " to deny the teleport");
        tc1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.PERSONAL + "Click to deny the teleport")));
        tc1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender.getUser().getName()));

        user.sendMessage(tc0);
        user.sendMessage(tc1);
        sender.sendPluginMessage(ChatColor.PERSONAL + "Send teleport request to " + ChatColor.VALUE + user.getChatName());
    }

    public static void teleportHereAsk(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendMessageCommandHelp("Ask a teleporthere", "tpha <player>");
            return;
        }

        if (!(sender.hasPermission("exbukkit.teleport.ask", 907) && sender.isPlayer(true))) {
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
                sender.sendPluginMessage(ChatColor.WARNING + "You have already send a teleport here request to " + user.getChatName());
                return;
            }
        } else {
            users = new Stack<>();
        }

        users.push(sender.getUser());
        Teleport.askHere.put(user, users);

        //user msg
        user.sendMessage(senderPlugin + ChatColor.VALUE + sender.getChatName() + ChatColor.PERSONAL + " requests a teleporthere");

        TextComponent tc0 = new TextComponent();
        tc0.setText(senderPlugin + ChatColor.PERSONAL + "Use " + ChatColor.VALUE + "/tpaccept " + sender.getUser().getChatName() + ChatColor.PERSONAL + " to accept the teleporthere");
        tc0.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.PERSONAL + "Click to accept the teleporthere")));
        tc0.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getUser().getName()));

        TextComponent tc1 = new TextComponent();
        tc1.setText(senderPlugin + ChatColor.PERSONAL + "Use " + ChatColor.VALUE + "/tpdeny " + sender.getUser().getChatName() + ChatColor.PERSONAL + " to deny the teleporthere");
        tc1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.PERSONAL + "Click to deny the teleporthere")));
        tc1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender.getUser().getName()));

        user.sendMessage(tc0);
        user.sendMessage(tc1);
        sender.sendPluginMessage(ChatColor.PERSONAL + "Send teleporthere request to " + ChatColor.VALUE + user.getChatName());
    }

    public static void accept(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission("exbukkit.teleport.ask.accept", 908)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();
        if (Teleport.ask.containsKey(user)) {
            Stack<User> users = Teleport.ask.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
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
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't an open request by " + argUser.getChatName());
                return;
            }

            users.pop();
            teleportUserToUser(user, argUser);
        } else if (Teleport.askHere.containsKey(user)) {
            Stack<User> users = Teleport.askHere.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
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
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't an open request by " + argUser.getChatName());
                return;
            }

            users.pop();
            teleportUserToUser(argUser, user);

        } else sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
    }

    private static void teleportUserToUser(User user, User teleporter) {
        teleporter.teleport(user);

        user.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + teleporter.getChatName());
        teleporter.sendMessage(Server.getChat().getSenderPlugin(Plugin.BUKKIT) + ChatColor.PERSONAL + "Teleported to " + ChatColor.VALUE + user.getChatName());
    }

    public static void deny(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission("exbukkit.teleport.ask.deny", 908)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();
        if (Teleport.ask.containsKey(user)) {
            Stack<User> users = Teleport.ask.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                sender.sendPluginMessage(ChatColor.PERSONAL + "Denied teleporthere request by " + enquirer.getChatName());
                enquirer.sendPluginMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " denied your teleport request");
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't an open request by " + argUser.getChatName());
                return;
            }

            users.pop();
            sender.sendPluginMessage(ChatColor.PERSONAL + "Denied teleport request by " + argUser.getChatName());
            argUser.sendPluginMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " denied your teleport request");

        } else if (Teleport.askHere.containsKey(user)) {
            Stack<User> users = Teleport.askHere.get(user);
            if (users.isEmpty()) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
                return;
            }

            if (args.isLengthEquals(0, false)) {
                User enquirer = users.pop();
                sender.sendPluginMessage(ChatColor.PERSONAL + "Denied teleporthere request by " + enquirer.getChatName());
                enquirer.sendPluginMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " denied your teleporthere request");
                return;
            }

            if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
                return;
            }

            User argUser = args.get(0).toUser();

            if (!users.contains(argUser)) {
                sender.sendPluginMessage(ChatColor.WARNING + "You haven't an open request by " + argUser.getChatName());
                return;
            }

            users.pop();
            sender.sendPluginMessage(ChatColor.PERSONAL + "Denied teleporthere request by " + argUser.getChatName());
            argUser.sendPluginMessage(Plugin.BUKKIT, ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " denied your teleporthere request");

        } else sender.sendPluginMessage(ChatColor.WARNING + "You haven't open requests");
    }

    public static void setSpawn(Sender sender, Arguments<Argument> args) {
        if (!sender.hasPermission("exbukkit.teleport.setspawn", 912)) {
            return;
        }

        if (sender.isPlayer(true)) {
            Player p = sender.getPlayer();
            if (args.isLengthEquals(3, false)) {
                if (!(args.get(0).isInt(true) && args.get(1).isInt(true) && args.get(2).isInt(true))) {
                    sender.sendMessageCommandHelp("Set world spawn", "setspawn");
                    sender.sendMessageCommandHelp("Set world spawn", "setspawn <x> <y> <z>");
                }

                p.getWorld().setSpawnLocation(new Location(p.getWorld(), args.get(0).toInt(), args.get(1).toInt(), args.get(2).toInt()));
                sender.sendPluginMessage(ChatColor.PERSONAL + "Set spawn in " + ChatColor.VALUE + p.getWorld().getName() + ChatColor.PERSONAL + " to: " + ChatColor.VALUE + args.get(0).getString() + " " + args.get(1).getString() + " " + args.get(2).getString());

            } else if (args.isLengthEquals(0, false)) {
                p.getWorld().setSpawnLocation(p.getLocation());
                sender.sendMessage(ChatColor.PERSONAL + "Set spawn" + " to: " + ChatColor.VALUE + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
            } else {
                sender.sendMessageTooFewManyArguments();
                sender.sendMessageCommandHelp("Set world spawn", "setspawn");
                sender.sendMessageCommandHelp("Set world spawn", "setspawn <x> <y> <z>");
            }

        } else if (sender instanceof ConsoleCommandSender) {
            if (!args.isLengthEquals(4, true)) {
                sender.sendMessageCommandHelp("Set world spawn", "setspawn <world> <x> <y> <z>");
                return;
            }

            if (!args.get(0).isWorldName(true)) {
                sender.sendMessageCommandHelp("Set world spawn", "setspawn <world> <x> <y> <z>");
                return;
            }

            ExWorld world = args.get(0).toWorld();
            if (!(args.get(1).isInt(true) && args.get(2).isInt(true) && args.get(3).isInt(true))) {
                sender.sendMessageCommandHelp("Set world spawn", "setspawn <world> <x> " + "<y> <z>");
                return;
            }

            world.setSpawnLocation(new ExLocation(world, args.get(1).toInt(), args.get(2).toInt(), args.get(3).toInt()));
            sender.sendPluginMessage(ChatColor.PERSONAL + "Set spawn in " + ChatColor.VALUE + world.getName() + ChatColor.PERSONAL + "to: " + ChatColor.VALUE + args.get(1).getString() + " " + args.get(2).getString() + " " + args.get(3).getString());
        }

    }

    public static void back(Sender sender, Arguments<Argument> args) {
        if (args.isLengthEquals(0, false) && sender.isPlayer(true)) {
            if (sender.hasPermission("exbukkit.teleport.back", 945)) {
                User user = sender.getUser();
                user.teleport(user.getLastLocation());
                sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to last location");
            }
        } else if (args.isLengthEquals(1, true) && args.get(0).isPlayerName(true)) {
            if (sender.hasPermission("exbukkit.teleport.back.other", 946)) {
                User user = args.get(0).toUser();
                user.teleport(user.getLastLocation());
                sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + user.getChatName() + " to last location");
                user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Teleported to last location");
            }
        }
    }

}
