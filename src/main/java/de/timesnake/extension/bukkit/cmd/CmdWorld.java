package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.basic.bukkit.util.world.WorldManager;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;

public class CmdWorld implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            this.sendCmdMessages(sender);
            return;
        }

        if (args.get(0).equalsIgnoreCase("list")) {
            if (!sender.hasPermission("exbukkit.world.list", 954)) {
                return;
            }

            StringBuilder sb = new StringBuilder(ChatColor.PERSONAL + "Worlds:");
            for (String wn : Server.getCommandManager().getTabCompleter().getWorldNames()) {
                sb.append(" ");
                sb.append(ChatColor.VALUE).append(wn);
                sb.append(ChatColor.PERSONAL).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sender.sendPluginMessage(sb.toString());
            return;
        }

        if (!args.isLengthHigherEquals(2, true)) {
            this.sendCmdMessages(sender);
            return;
        }

        String worldName = args.getString(1);
        ExWorld world = Server.getWorld(worldName);

        switch (args.getString(0)) {
            case "create" -> {
                if (!sender.hasPermission("exbukkit.world.create", 949)) {
                    return;
                }
                if (world != null) {
                    sender.sendMessageWorldAlreadyExist(worldName);
                    return;
                }
                for (String s : WorldManager.UNSUPPORTED_SYMBOLS) {
                    if (worldName.contains(s)) {
                        sender.sendPluginMessage(ChatColor.WARNING + "World name contains an unsupported symbol: " + ChatColor.VALUE + s);
                        return;
                    }
                }
                WorldManager.Type worldType = WorldManager.Type.VOID;
                if (args.isLengthEquals(3, false)) {
                    String typeName = args.getString(2).toUpperCase();
                    try {
                        worldType = WorldManager.Type.valueOf(typeName);
                    } catch (IllegalArgumentException e) {
                        sender.sendPluginMessage(ChatColor.WARNING + "World-Type " + ChatColor.VALUE + typeName + ChatColor.WARNING + " does not exist");
                        return;
                    }
                } else if (args.isLengthHigherEquals(4, true)) {
                    sender.sendMessageTooManyArguments();
                    return;
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Creating world " + ChatColor.VALUE + worldName + ChatColor.PERSONAL + " with type " + ChatColor.VALUE + worldType.name());
                Server.getWorldManager().createWorld(worldName, worldType);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Complete");
            }
            case "clone" -> {
                if (!sender.hasPermission("exbukkit.world.clone", 1)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (!args.isLengthEquals(3, true)) {
                    return;
                }
                Argument clonedName = args.get(2);
                if (clonedName.isWorldName(false)) {
                    sender.sendMessageWorldAlreadyExist(worldName);
                    return;
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Cloning world " + ChatColor.VALUE + worldName + ChatColor.PERSONAL + " to " + ChatColor.VALUE + clonedName.getString());
                Server.getWorldManager().cloneWorld(clonedName.getString(), world);
                sender.sendPluginMessage(ChatColor.PERSONAL + "Complete");
            }
            case "delete" -> {
                if (!sender.hasPermission("exbukkit.world.delete", 951)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (Server.getWorldManager().deleteWorld(world, true)) {
                    sender.sendPluginMessage(ChatColor.PERSONAL + "Deleted world " + ChatColor.VALUE + worldName);
                } else {
                    sender.sendPluginMessage(ChatColor.WARNING + "Failed to delete world " + ChatColor.VALUE + worldName);
                }
            }
            case "unload" -> {
                if (!sender.hasPermission("exbukkit.world.unload", 956)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (Server.getWorldManager().unloadWorld(world, true)) {
                    sender.sendPluginMessage(ChatColor.PERSONAL + "Unloaded world " + ChatColor.VALUE + worldName);
                } else {
                    sender.sendPluginMessage(ChatColor.WARNING + "Failed to unload world " + ChatColor.VALUE + worldName);
                }
            }
            case "teleport", "tp" -> {
                if (!sender.hasPermission("exbukkit.world.teleport", 952)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (args.isLengthEquals(2, false)) {
                    if (!sender.isPlayer(true)) {
                        return;
                    }
                    sender.getUser().teleport(world);
                    sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported to world " + ChatColor.VALUE + worldName);
                } else if (args.isLengthEquals(3, true)) {
                    if (!args.get(2).isPlayerName(true)) {
                        return;
                    }
                    User user = args.get(2).toUser();
                    user.teleport(world);
                    user.sendPluginMessage(Plugin.BUKKIT,
                            ChatColor.PERSONAL + "Teleported to world " + ChatColor.VALUE + worldName);
                    sender.sendPluginMessage(ChatColor.PERSONAL + "Teleported " + ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " to world " + ChatColor.VALUE + worldName);
                }
            }
            case "rename" -> {
                if (!sender.hasPermission("exbukkit.world.rename", 957)) {
                    return;
                }

                if (!args.isLengthEquals(3, true)) {
                    sender.sendMessageCommandHelp("Rename a world", "mw rename <world> <newName>");
                    return;
                }

                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }

                String newName = args.getString(2);

                if (Server.getWorld(newName) != null) {
                    sender.sendMessageWorldAlreadyExist(worldName);
                    return;
                }

                File worldFolder = world.getWorldFolder();

                boolean result = Server.getWorldManager().unloadWorld(world, true);

                if (!result) {
                    sender.sendPluginMessage(ChatColor.WARNING + "Can not unload world " + world.getName());
                    return;
                }

                worldFolder.renameTo(new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator +
                        newName));

                Server.getWorldManager().createWorld(newName);
            }
            default -> this.sendCmdMessages(sender);
        }
    }

    private void sendCmdMessages(Sender sender) {
        if (sender.hasPermission("exbukkit.world.create")) {
            sender.sendMessageCommandHelp("Create a new world", "mw create <world> [type]");
        }
        if (sender.hasPermission("exbukkit.world.delete")) {
            sender.sendMessageCommandHelp("Delete an existing world", "mw delete <world>");
        }
        if (sender.hasPermission("exbukkit.world.unload")) {
            sender.sendMessageCommandHelp("Unload an existing world", "mw unload <world>");
        }
        if (sender.hasPermission("exbukkit.world.teleport")) {
            sender.sendMessageCommandHelp("Teleport to a world", "mw tp <world> [player]");
        }
        if (sender.hasPermission("exbukkit.world.clone")) {
            sender.sendMessageCommandHelp("Clone a world", "mw clone <source> <world>");
        }
        if (sender.hasPermission("exbukkit.world.rename")) {
            sender.sendMessageCommandHelp("Rename a world", "mw rename <world> <newName>");
        }
        if (sender.hasPermission("exbukkit.world.list")) {
            sender.sendMessageCommandHelp("List the worlds", "mw list");
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() >= 1) {
            if (args.getLength() == 1) {
                return List.of("create", "tp", "delete", "clone", "list", "unload", "rename");
            }
            switch (args.getString(0).toLowerCase()) {
                case "create":
                    if (args.getLength() == 3) {
                        return WorldManager.Type.getNames();
                    }
                    break;
                case "tp":
                    if (args.getLength() == 3) {
                        return Server.getCommandManager().getTabCompleter().getPlayerNames();
                    }
                case "delete":
                case "unload":
                case "clone":
                case "rename":
                    if (args.getLength() == 2) {
                        return Server.getCommandManager().getTabCompleter().getWorldNames();
                    }
                    break;
            }
        }
        return List.of();
    }
}
