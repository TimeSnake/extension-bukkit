/*
 * extension-bukkit.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.core.world.CustomGenerator;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.basic.bukkit.util.world.ExWorldType;
import de.timesnake.basic.bukkit.util.world.WorldManager;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Chat;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CmdWorld implements CommandListener {

    private Code.Permission listPerm;
    private Code.Permission createPerm;
    private Code.Permission clonePerm;
    private Code.Permission deletePerm;
    private Code.Permission unloadPerm;
    private Code.Permission teleportPerm;
    private Code.Permission renamePerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            this.sendCmdMessages(sender);
            return;
        }

        if (args.get(0).equalsIgnoreCase("list")) {
            if (!sender.hasPermission(this.listPerm)) {
                return;
            }

            sender.sendPluginMessage(Component.text("Worlds: ", ExTextColor.PERSONAL)
                    .append(Chat.listToComponent(Server.getCommandManager().getTabCompleter().getWorldNames(),
                            ExTextColor.VALUE, ExTextColor.PERSONAL)));
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
                if (!sender.hasPermission(this.createPerm)) {
                    return;
                }

                if (world != null) {
                    sender.sendMessageWorldAlreadyExist(worldName);
                    return;
                }

                for (String s : WorldManager.UNSUPPORTED_SYMBOLS) {
                    if (worldName.contains(s)) {
                        sender.sendPluginMessage(Component.text("World name contains an unsupported symbol: ", ExTextColor.WARNING)
                                .append(Component.text(s, ExTextColor.VALUE)));
                    }
                }

                ExWorldType worldType = ExWorldType.VOID;
                if (args.isLengthEquals(3, false)) {
                    String worldTypeName = args.getString(2).toLowerCase();
                    worldType = ExWorldType.valueOf(worldTypeName);
                    if (worldType == null) {
                        sender.sendPluginMessage(Component.text("World-Type ", ExTextColor.WARNING)
                                .append(Component.text(worldTypeName, ExTextColor.VALUE))
                                .append(Component.text(" does not exist", ExTextColor.WARNING)));
                        return;
                    }
                } else if (!args.isLengthEquals(4, false)) {
                    if (args.isLengthHigherEquals(5, true)) {
                        return;
                    }
                } else if (!args.get(2).equalsIgnoreCase("custom")) {
                    sender.sendMessageTooManyArguments();
                } else {
                    String materialsString = args.getString(3);
                    List<Tuple<Integer, Material>> materials = new LinkedList<>();
                    String[] materialStrings = materialsString.split(";");

                    for (String heightMaterialString : materialStrings) {
                        String[] heightMaterial = heightMaterialString.split("#");
                        if (heightMaterial.length == 1) {
                            Material material;
                            try {
                                material = Material.valueOf(heightMaterial[0].toUpperCase());
                            } catch (IllegalArgumentException var21) {
                                sender.sendMessageNoItemName(heightMaterial[0].toUpperCase());
                                return;
                            }

                            materials.add(new Tuple<>(1, material));
                        } else {
                            if (heightMaterial.length != 2) {
                                sender.sendPluginMessage(Component.text("Invalid layer ", ExTextColor.WARNING).append(Component.text(heightMaterialString, ExTextColor.VALUE)));
                                return;
                            }

                            Material material;
                            int height;
                            try {
                                height = Integer.parseInt(heightMaterial[0]);
                                material = Material.valueOf(heightMaterial[1].toUpperCase());
                            } catch (NumberFormatException var19) {
                                sender.sendMessageNoInteger(heightMaterial[0]);
                                return;
                            } catch (IllegalArgumentException var20) {
                                sender.sendMessageNoItemName(heightMaterial[1].toUpperCase());
                                return;
                            }

                            materials.add(new Tuple<>(height, material));
                        }
                    }

                    worldType = new ExWorldType("custom", World.Environment.NORMAL, null,
                            new CustomGenerator(materials));
                }

                sender.sendPluginMessage(Component.text("Creating world ", ExTextColor.PERSONAL)
                        .append(Component.text(worldName, ExTextColor.VALUE, TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Component.text("Click to teleport to world")))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/mw tp " + worldName)))
                        .append(Component.text(" with type ", ExTextColor.PERSONAL))
                        .append(Component.text(worldType.getName(), ExTextColor.VALUE)));
                ExWorld createdWorld = Server.getWorldManager().createWorld(worldName, worldType);
                if (createdWorld != null) {
                    sender.sendPluginMessage(Component.text("Complete", ExTextColor.PERSONAL));
                } else {
                    sender.sendPluginMessage(Component.text("Failed to load world ", ExTextColor.WARNING).append(Component.text(worldName, ExTextColor.VALUE)));
                }
            }
            case "clone" -> {
                if (!sender.hasPermission(this.clonePerm)) {
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
                sender.sendPluginMessage(Component.text("Cloning world ", ExTextColor.PERSONAL)
                        .append(Component.text(worldName, ExTextColor.VALUE))
                        .append(Component.text(" to ", ExTextColor.PERSONAL))
                        .append(Component.text(clonedName.getString(), ExTextColor.VALUE)));
                Server.getWorldManager().cloneWorld(clonedName.getString(), world);
                sender.sendPluginMessage(Component.text("Complete", ExTextColor.PERSONAL));
            }
            case "delete" -> {
                if (!sender.hasPermission(this.deletePerm)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (Server.getWorldManager().deleteWorld(world, true)) {
                    sender.sendPluginMessage(Component.text("Deleted world ", ExTextColor.PERSONAL)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                } else {
                    sender.sendPluginMessage(Component.text("Failed to delete world ", ExTextColor.WARNING)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                }
            }
            case "unload" -> {
                if (!sender.hasPermission(this.unloadPerm)) {
                    return;
                }
                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }
                if (Server.getWorldManager().unloadWorld(world, true)) {
                    sender.sendPluginMessage(Component.text("Unloaded world ", ExTextColor.PERSONAL)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                } else {
                    sender.sendPluginMessage(Component.text("Failed to unload world ", ExTextColor.WARNING)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                }
            }
            case "teleport", "tp" -> {
                if (!sender.hasPermission(this.teleportPerm)) {
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
                    sender.sendPluginMessage(Component.text("Teleported to world ", ExTextColor.PERSONAL)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                } else if (args.isLengthEquals(3, true)) {
                    if (!args.get(2).isPlayerName(true)) {
                        return;
                    }
                    User user = args.get(2).toUser();
                    user.teleport(world);
                    user.sendPluginMessage(Plugin.BUKKIT, Component.text("Teleported to world ", ExTextColor.PERSONAL)
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                    sender.sendPluginMessage(Component.text("Teleported ", ExTextColor.PERSONAL)
                            .append(user.getChatNameComponent())
                            .append(Component.text(" to world ", ExTextColor.PERSONAL))
                            .append(Component.text(worldName, ExTextColor.VALUE)));
                }
            }
            case "rename" -> {
                if (!sender.hasPermission(this.renamePerm)) {
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
                    sender.sendPluginMessage(Component.text("Can not unload world ", ExTextColor.WARNING)
                            .append(Component.text(world.getName(), ExTextColor.VALUE)));
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
                        return ExWorldType.getNames();
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

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.listPerm = plugin.createPermssionCode("wol", "exbukkit.world.list");
        this.createPerm = plugin.createPermssionCode("wol", "exbukkit.world.create");
        this.clonePerm = plugin.createPermssionCode("wol", "exbukkit.world.clone");
        this.deletePerm = plugin.createPermssionCode("wol", "exbukkit.world.delete");
        this.unloadPerm = plugin.createPermssionCode("wol", "exbukkit.world.unload");
        this.teleportPerm = plugin.createPermssionCode("wol", "exbukkit.world.teleport");
        this.renamePerm = plugin.createPermssionCode("wol", "exbukkit.world.rename");
    }
}
