/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserQuitEvent;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import de.timesnake.library.extension.util.chat.Code;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class CmdTeleport implements CommandListener, Listener {

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport");
  private final Code teleportToOtherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.toother");
  private final Code teleportToOtherOtherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.toother.other");
  private final Code teleportLocationPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.location");
  private final Code teleportLocationOtherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.location.other");
  private final Code teleportHerePerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.here");
  private final Code teleportWorldPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.world");
  private final Code teleportWorldOtherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.world.other");
  private final Code teleportSpawnPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.spawn");
  private final Code teleportSetSpawnPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.setspawn");
  private final Code teleportAskPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.ask");
  private final Code teleportAskAcceptPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.ask.accept");
  private final Code teleportAskDenyPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.ask.deny");
  private final Code teleportBackPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.back");
  private final Code teleportBackOtherPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.teleport.back.other");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    switch (cmd.getName().toLowerCase()) {
      case "tp", "teleport" -> this.teleport(sender, args);
      case "tph", "tphere", "teleporthere" -> this.teleportHere(sender, args);

      //tpa
      case "tpa", "call", "tpask", "teleportask" -> this.teleportAsk(sender, args);

      //tpahere
      case "tpahere", "tpaskhere", "tpah", "tpaskh", "teleportaskhere" -> this.teleportHereAsk(sender, args);

      //tpa settings
      case "tpaccept" -> this.accept(sender, args);
      case "tpdeny" -> this.deny(sender, args);

      //spawn
      case "spawn" -> this.teleportSpawn(sender);
      case "setspawn" -> this.setSpawn(sender, args);
      case "tpback" -> this.back(sender, args);

      // tphall
      case "tphall", "tphereall" -> this.teleportHereAll(sender);
    }

  }

  @Override
  public Completion getTabCompletion() {
    return new Completion()
        .addArgument((sender, cmd, args) -> cmd.equalsIgnoreCase("tp"), Completion.ofPlayerNames().values("~", "0")
            .addArgument((sender, cmd, args) -> args.get(0).isPlayerName(false), Completion.ofPlayerNames())
            .addArgument((sender, cmd, args) -> !args.get(0).isPlayerName(false), new Completion("~", "0")
                .addArgument(new Completion("~", "0"))))
        .addArgument((sender, cmd, args) -> List.of("tph", "tphere", "teleporthere", "tpahere", "tpahere",
                "tpaskhere", "tpah", "tpaskh", "tpa", "call", "tpask", "teleportask").contains(cmd.toLowerCase()),
            Completion.ofPlayerNames())
        .addArgument((sender, cmd, args) -> cmd.equalsIgnoreCase("setspawn"), new Completion("~")
            .addArgument(new Completion("~")
                .addArgument(new Completion("~"))));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void teleport(Sender sender, Arguments<Argument> args) {
    if (args.isLengthEquals(1, false)) {
      sender.hasPermissionElseExit(this.teleportToOtherPerm);
      sender.isPlayerElseExit(true);

      args.get(0).assertElseExit(a -> a.isPlayerName(true));

      User user = sender.getUser();
      User to = args.get(0).toUser();

      user.teleport(to);
      sender.sendPluginTDMessage("§sTeleported to " + to.getTDChatName());

    } else if (args.isLengthEquals(2, false)) {
      if (!((sender.hasPermission(teleportToOtherPerm.getPermission()) && sender.isPlayer(false)
          && sender.getPlayer().getName().equals(args.getString(0)))
          || sender.hasPermission(teleportToOtherOtherPerm))) {
        return;
      }

      args.get(0).assertElseExit(a -> a.isPlayerName(true));
      args.get(1).assertElseExit(a -> a.isPlayerName(true));

      User user1 = args.get(0).toUser();
      User user2 = args.get(1).toUser();

      user1.teleport(user2);

      user1.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to " + user2.getTDChatName());

      if (!sender.getName().equals(user1.getName()) && !sender.getName().equals(user2.getName())) {
        sender.sendPluginTDMessage("§sTeleported " + user1.getTDChatName() + "§s to " + user2.getTDChatName());
      }
    } else if (args.isLengthEquals(3, false)) {
      sender.hasPermissionElseExit(this.teleportLocationPerm);
      sender.isPlayerElseExit(true);

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
      sender.sendPluginTDMessage("§sTeleported to §v" + x + " " + y + " " + z);

    } else if (args.isLengthEquals(4, false)) {
      sender.hasPermissionElseExit(this.teleportLocationOtherPerm);
      args.get(0).assertElseExit(a -> a.isPlayerName(true));

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

      user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to §v" + x + " " + y + " " + z);

      if (!sender.getName().equals(user.getName())) {
        sender.sendPluginTDMessage("§sTeleported " + user.getTDChatName() + "§s to §v" + x + " " + y + " " + z);
      }
    } else {
      sender.sendMessageTooFewManyArguments();
      sender.sendTDMessageCommandHelp("Teleport to player", "tp <player>");
      sender.sendTDMessageCommandHelp("Teleport player to player", "tp <player> <player>");
      sender.sendTDMessageCommandHelp("Teleport to location", "tp <x> <y> <z>");
      sender.sendTDMessageCommandHelp("Teleport player to location", "tp <player> <x> <y> <z>");
    }

  }

  public void teleportHere(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Teleport player here", "tph <player>");
      return;
    }

    sender.hasPermissionElseExit(this.teleportHerePerm);
    sender.isPlayer(true);
    args.get(0).assertElseExit(a -> a.isPlayerName(true));

    User user = sender.getUser();
    User other = args.get(0).toUser();

    other.teleport(user);

    other.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to " + user.getTDChatName());
    sender.sendPluginTDMessage("§sTeleported " + other.getTDChatName());
  }

  public void teleportWorld(Sender sender, Arguments<Argument> args) {
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

          user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to §v" + world.getName() + "§s spawn");

          if (!sender.getName().equals(user.getName())) {
            sender.sendPluginTDMessage("§sTeleported" + user.getTDChatName() + "§s to §v" + world.getName() + "§s spawn");
          }
        } else if (args.isLengthEquals(5, false)) {
          if (!(args.get(2).isInt(true) && args.get(3).isInt(true) && args.get(4).isInt(true))) {
            return;
          }

          int x = args.get(2).toInt();
          int y = args.get(3).toInt();
          int z = args.get(4).toInt();

          Location loc = new ExLocation(world, x, y, z);
          world.loadChunk(loc.getChunk());
          user.teleport(loc);

          user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to §v" + world.getName() + " " + x + " " + y + " " + z);
          if (!sender.getName().equals(user.getName())) {
            sender.sendPluginTDMessage("§sTeleported" + user.getTDChatName() + "§s to §v" + world.getName() + " " + x + " " + y + " " + z);
          }
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] " + "[y] [z]");
          sender.sendTDMessageCommandHelp("Teleport player to world", "tpw " + "<player> <world> [x] [y] [z]");
        }
      } else if (args.get(0).isWorldName(false)) {
        sender.hasPermissionElseExit(this.teleportWorldPerm);
        sender.isPlayerElseExit(true);
        args.get(0).assertElseExit(a -> a.isWorldName(true));

        ExWorld world = args.get(0).toWorld();

        if (args.isLengthEquals(1, false)) {
          sender.getUser().teleport(world);

          sender.sendPluginTDMessage("§sTeleported to " + world.getName() + "§s spawn");
        } else if (args.isLengthEquals(4, false)) {
          int x = args.get(1).toIntOrExit(true);
          int y = args.get(2).toIntOrExit(true);
          int z = args.get(3).toIntOrExit(true);

          Location loc = new ExLocation(world, x, y, z);
          world.loadChunk(loc.getChunk());
          sender.getUser().teleport(loc);

          sender.sendPluginTDMessage("§sTeleported to §v" + world.getName() + " " + x + " " + y + " " + z);
        } else {
          sender.sendMessageTooFewManyArguments();
          sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
          sender.sendTDMessageCommandHelp("Teleport player to world", "tpw <player> " + "<world> [x] [y] [z]");
        }
      } else {
        sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
        sender.sendTDMessageCommandHelp("Teleport player to world", "tpw <player> <world> [x] " + "[y] [z]");
      }
    } else {
      sender.sendTDMessageCommandHelp("Teleport to world", "tpw <world> [x] [y] [z]");
      sender.sendTDMessageCommandHelp("Teleport player to world", "tpw <player> <world> [x] [y] " + "[z]");
    }
  }

  public void teleportSpawn(Sender sender) {
    sender.hasPermissionElseExit(this.teleportSpawnPerm);
    sender.isPlayerElseExit(true);

    sender.getPlayer().teleport(sender.getPlayer().getWorld().getSpawnLocation());
  }

  public void teleportAsk(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Ask a teleport", "tpa <player>");
      return;
    }

    sender.hasPermissionElseExit(this.teleportAskPerm);
    sender.isPlayerElseExit(true);
    args.get(0).assertElseExit(a -> a.isPlayerName(true));

    User user = args.get(0).toUser();

    Stack<User> users = this.ask.get(user);

    if (users != null) {
      if (users.contains(sender.getUser())) {
        sender.sendPluginTDMessage("§wYou have already send a teleport request to " + user.getTDChatName());
        return;
      }
    } else {
      users = new Stack<>();
    }

    users.push(sender.getUser());
    this.ask.put(user, users);

    //user msg
    user.sendPluginTDMessage(Plugin.BUKKIT, sender.getUser().getTDChatName() + "§s requests a teleport");

    user.sendClickablePluginMessage(Plugin.BUKKIT, "§sUse §v§u/tpaccept " + sender.getUser().getTDChatName()
            + "§s to accept the teleport request",
        "/tpaccept ",
        "Click to accept the teleport request",
        net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

    user.sendClickablePluginMessage(Plugin.BUKKIT, "§sUse §v§u/tpdeny " + sender.getUser().getTDChatName()
            + "§s to deny the teleport request",
        "/tpdeny ",
        "Click to deny the teleport request",
        net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

    sender.sendPluginTDMessage("§sSend teleport request to " + user.getTDChatName());
  }

  public void teleportHereAsk(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Ask a teleporthere", "tpah <player>");
      return;
    }

    sender.hasPermissionElseExit(this.teleportAskPerm);
    sender.isPlayerElseExit(true);
    args.get(0).assertElseExit(a -> a.isPlayerName(true));

    User user = args.get(0).toUser();

    Stack<User> users = this.askHere.get(user);

    if (this.askHere.containsKey(user)) {
      if (users.contains(sender.getUser())) {
        sender.sendPluginTDMessage("§wYou have already send a teleport here request to " + user.getTDChatName());
        return;
      }
    } else {
      users = new Stack<>();
    }

    users.push(sender.getUser());
    this.askHere.put(user, users);

    //user msg
    user.sendPluginTDMessage(Plugin.BUKKIT, sender.getUser().getTDChatName() + "§s requests a teleport-here");

    user.sendClickablePluginMessage(Plugin.BUKKIT, "§sUse §v§u/tpaccept " + sender.getUser().getTDChatName()
            + "§s to accept the teleport-here request",
        "/tpaccept ",
        "Click to accept the teleport-here request",
        net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

    user.sendClickablePluginMessage(Plugin.BUKKIT, "§sUse §v§u/tpdeny " + sender.getUser().getTDChatName()
            + "§s to deny the teleport-here request",
        "/tpdeny ",
        "Click to deny the teleport-her request",
        net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);

    sender.sendPluginTDMessage("§sSend teleport-here request to " + user.getTDChatName());
  }

  public void accept(Sender sender, Arguments<Argument> args) {
    if (!sender.hasPermission(teleportAskAcceptPerm)) {
      return;
    }

    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();
    if (this.ask.containsKey(user)) {
      Stack<User> users = this.ask.get(user);
      if (users.isEmpty()) {
        sender.sendPluginTDMessage("§wYou have no open requests");
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
        sender.sendPluginTDMessage("§wYou have no open request by " + argUser.getTDChatName());
        return;
      }

      users.pop();
      teleportUserToUser(user, argUser);
    } else if (this.askHere.containsKey(user)) {
      Stack<User> users = this.askHere.get(user);
      if (users.isEmpty()) {
        sender.sendPluginTDMessage("§wYou have no open requests");
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
        sender.sendPluginTDMessage("§wYou have no open request by " + argUser.getTDChatName());
        return;
      }

      users.pop();
      teleportUserToUser(argUser, user);

    } else {
      sender.sendPluginTDMessage("§wYou have no open requests");
    }
  }

  private void teleportUserToUser(User user, User teleporter) {
    teleporter.teleport(user);

    user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported " + teleporter.getTDChatName());
    teleporter.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to " + user.getTDChatName());
  }

  public void deny(Sender sender, Arguments<Argument> args) {
    if (!sender.hasPermission(teleportAskDenyPerm)) {
      return;
    }

    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();
    if (this.ask.containsKey(user)) {
      Stack<User> users = this.ask.get(user);
      if (users.isEmpty()) {
        sender.sendPluginTDMessage("§wYou have no open requests");
        return;
      }

      if (args.isLengthEquals(0, false)) {
        User enquirer = users.pop();
        sender.sendPluginTDMessage("§sDenied teleport request by " + enquirer.getTDChatName());
        enquirer.sendPluginTDMessage(Plugin.BUKKIT, user.getTDChatName() + "§s denied your teleport request");
        return;
      }

      if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
        return;
      }

      User argUser = args.get(0).toUser();

      if (!users.contains(argUser)) {
        sender.sendPluginTDMessage("§wYou have no open request by " + argUser.getTDChatName());
        return;
      }

      users.pop();
      sender.sendPluginTDMessage("§sDenied teleport request by " + argUser.getTDChatName());
      argUser.sendPluginTDMessage(Plugin.BUKKIT, user.getTDChatName() + "§s denied your teleport request");

    } else if (this.askHere.containsKey(user)) {
      Stack<User> users = this.askHere.get(user);
      if (users.isEmpty()) {
        sender.sendPluginTDMessage("§wYou have no open requests");
        return;
      }

      if (args.isLengthEquals(0, false)) {
        User enquirer = users.pop();
        sender.sendPluginTDMessage("§sDenied teleport-here request by " + enquirer.getTDChatName());
        enquirer.sendPluginTDMessage(Plugin.BUKKIT, user.getTDChatName() + "§s denied your teleport-here request");
        return;
      }

      if (!(args.isLengthEquals(1, true) && args.get(0).isPlayerName(true))) {
        return;
      }

      User argUser = args.get(0).toUser();

      if (!users.contains(argUser)) {
        sender.sendPluginTDMessage("§wYou have no open request by " + argUser.getTDChatName());
        return;
      }

      users.pop();
      sender.sendPluginTDMessage("§sDenied teleport-here request by " + argUser.getTDChatName());
      argUser.sendPluginTDMessage(Plugin.BUKKIT, user.getTDChatName() + "§s denied your teleport-here request");

    } else {
      sender.sendPluginTDMessage("§sYou have no open requests");
    }
  }

  public void setSpawn(Sender sender, Arguments<Argument> args) {
    if (!sender.hasPermission(teleportSetSpawnPerm)) {
      return;
    }

    if (sender.isPlayer(true)) {
      Player p = sender.getPlayer();
      if (args.isLengthEquals(3, false)) {
        int x = args.get(0).toIntOrExit(true);
        int y = args.get(1).toIntOrExit(true);
        int z = args.get(2).toIntOrExit(true);

        p.getWorld().setSpawnLocation(new Location(p.getWorld(), x, y, z));

        sender.sendPluginTDMessage("§sSet spawn in §v" + p.getWorld().getName() + "§s to §v" + x + " " + y + " " + z);

      } else if (args.isLengthEquals(0, false)) {
        p.getWorld().setSpawnLocation(p.getLocation());
        sender.sendTDMessage("§sSet spawn to: §v" + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY()
            + " " + p.getLocation().getBlockZ());
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

      int x = args.get(1).toIntOrExit(true);
      int y = args.get(2).toIntOrExit(true);
      int z = args.get(3).toIntOrExit(true);

      world.setSpawnLocation(new ExLocation(world, x, y, z));
      sender.sendPluginTDMessage("§Set spawn in §v" + world.getName() + "§sto: §v" + x + " " + y + " " + z);
    }

  }

  public void back(Sender sender, Arguments<Argument> args) {
    if (args.isLengthEquals(0, false) && sender.isPlayer(true)) {
      if (sender.hasPermission(teleportBackPerm)) {
        User user = sender.getUser();
        user.teleport(user.getLastLocation());
        sender.sendPluginTDMessage("§sTeleported to last location");
      }
    } else if (args.isLengthEquals(1, true) && args.get(0).isPlayerName(true)) {
      if (sender.hasPermission(teleportBackOtherPerm)) {
        User user = args.get(0).toUser();
        user.teleport(user.getLastLocation());
        sender.sendPluginTDMessage("§sTeleported" + user.getTDChatName() + "§s to last location");
        user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to last location");
      }
    }
  }

  public void teleportHereAll(Sender sender) {
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
        user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to" + senderUser.getTDChatName());
      }
    }

    sender.sendPluginTDMessage("§sTeleported §vall");
  }

  private static final HashMap<User, Stack<User>> ask = new HashMap<>();
  private static final HashMap<User, Stack<User>> askHere = new HashMap<>();


  @EventHandler
  public void onUserQuit(UserQuitEvent e) {
    User user = e.getUser();
    this.ask.remove(user);
    this.askHere.remove(user);

    for (Stack<User> users : ask.values()) {
      users.remove(user);
    }

    for (Stack<User> users : askHere.values()) {
      users.remove(user);
    }
  }
}
