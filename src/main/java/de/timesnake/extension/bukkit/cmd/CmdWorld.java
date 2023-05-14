/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ExCommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.basic.bukkit.util.world.ExWorldType;
import de.timesnake.basic.bukkit.util.world.WorldManager;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.extension.bukkit.main.ExBukkit;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Chat;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.CmdOption;
import de.timesnake.library.extension.util.cmd.ExArguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class CmdWorld implements ExCommandListener, Listener {

    private final Map<String, Sender> waitingWorldLoadedSenderByWorldName = new HashMap<>();
    private Code listPerm;
    private Code createPerm;
    private Code clonePerm;
    private Code deletePerm;
    private Code unloadPerm;
    private Code teleportPerm;
    private Code renamePerm;

    public CmdWorld() {
        Server.registerListener(this, ExBukkit.getPlugin());
    }

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd,
            ExArguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            this.sendCmdMessages(sender);
            return;
        }

        if (args.get(0).equalsIgnoreCase("list")) {
            if (!sender.hasPermission(this.listPerm)) {
                return;
            }

            sender.sendPluginMessage(Component.text("Worlds: ", ExTextColor.PERSONAL)
                    .append(Chat.listToComponent(
                            Server.getCommandManager().getTabCompleter().getWorldNames(),
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
                this.handleWorldCreation(sender, args, world, worldName);
            }
            case "clone" -> {
                this.handleWorldClone(sender, args, world, worldName);
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
                    sender.sendPluginTDMessage("§sDeleted world §v" + worldName);
                } else {
                    sender.sendPluginTDMessage("§wFailed to delete world §v" + worldName);
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
                    sender.sendPluginTDMessage("§sUnloaded world §v" + worldName);
                } else {
                    sender.sendPluginTDMessage("§wFailed to unload world §v" + worldName);
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
                    sender.sendPluginTDMessage("§sTeleported to world §v" + worldName);
                } else if (args.isLengthEquals(3, true)) {
                    if (!args.get(2).isPlayerName(true)) {
                        return;
                    }
                    User user = args.get(2).toUser();
                    user.teleport(world);
                    user.sendPluginTDMessage(Plugin.BUKKIT, "§sTeleported to world §v" + worldName);
                    sender.sendPluginTDMessage("§sTeleported " + user.getChatName()
                            + "§v to world §v" + worldName);
                }
            }
            case "rename" -> {
                if (!sender.hasPermission(this.renamePerm)) {
                    return;
                }

                if (!args.isLengthEquals(3, true)) {
                    sender.sendTDMessageCommandHelp("Rename a world",
                            "mw rename <world> <newName>");
                    return;
                }

                if (world == null) {
                    sender.sendMessageWorldNotExist(worldName);
                    return;
                }

                String newName = args.getString(2);

                for (String s : WorldManager.UNSUPPORTED_SYMBOLS) {
                    if (newName.contains(s)) {
                        sender.sendPluginTDMessage(
                                "§wWorld name contains an unsupported symbol: §v" + s);
                        return;
                    }
                }

                sender.assertElseExitWith(Server.getWorld(newName) == null,
                        s -> s.sendMessageWorldAlreadyExist(newName));

                File worldFolder = world.getWorldFolder();

                boolean result = Server.getWorldManager().unloadWorld(world, true);

                if (!result) {
                    sender.sendPluginTDMessage("§wCan not unload world §v" + world.getName());
                    return;
                }

                worldFolder.renameTo(new File(Bukkit.getWorldContainer().getAbsolutePath()
                        + File.separator + newName));

                Server.getWorldManager().createWorld(newName);
            }
            default -> this.sendCmdMessages(sender);
        }
    }

    private void handleWorldClone(Sender sender, ExArguments<Argument> args, ExWorld world,
            String worldName) {

        sender.hasPermissionElseExit(this.clonePerm);
        sender.assertElseExitWith(world != null, s -> s.sendMessageWorldNotExist(worldName));
        args.assertElseExit(a -> a.isLengthEquals(3, true));

        Argument clonedName = args.get(2);
        if (clonedName.isWorldName(false)) {
            sender.sendMessageWorldAlreadyExist(worldName);
            return;
        }

        sender.sendPluginTDMessage(
                "§sCloning world §v" + worldName + "§s to §v" + clonedName.getString());

        Server.getWorldManager().cloneWorld(clonedName.getString(), world);
        sender.sendPluginTDMessage("§sComplete");
    }

    private void handleWorldCreation(Sender sender, ExArguments<Argument> args, ExWorld world,
            String worldName) {
        sender.hasPermissionElseExit(this.createPerm);

        if (args.containsFlag('p')) {
            sender.assertElseExitWith(world != null,
                    s -> s.sendMessageWorldNotExist(worldName));

            if (!world.isTemporary()) {
                sender.sendPluginTDMessage("§wWorld §v" + worldName + "§w is not temporary");
                return;
            }

            world.makePersistent();
            sender.sendPluginTDMessage("§sMade world §v" + worldName + "§s persistent");
            return;
        }

        sender.assertElseExitWith(world == null,
                s -> s.sendMessageWorldAlreadyExist(worldName));

        for (String s : WorldManager.UNSUPPORTED_SYMBOLS) {
            if (worldName.contains(s)) {
                sender.sendPluginTDMessage("§wWorld name contains an unsupported symbol: §v" + s);
                return;
            }
        }

        args.assertElseExit(a -> a.isLengthHigherEquals(2, true));

        ExWorldType worldType = ExWorldType.VOID;

        if (args.length() == 3 && args.getOptions().isEmpty()) {
            String worldTypeName = args.getString(2).toLowerCase();
            worldType = ExWorldType.valueOf(worldTypeName);
            if (worldType == null) {
                sender.sendPluginTDMessage("§wWorld-Type §v" + worldTypeName + "§w does not exist");
                return;
            }
        } else if (args.length() >= 3) {
            if (!args.get(2).getString().startsWith("custom_")) {
                sender.sendMessageTooManyArguments();
            } else if (args.get(2).equalsIgnoreCase("custom_flat")) {
                if (!args.isLengthEquals(4, true)) {
                    return;
                }

                String materialsString = args.getString(3);
                List<Tuple<Integer, Material>> materials = this.parseMaterials(sender,
                        materialsString);

                if (materials == null) {
                    return;
                }

                worldType = new ExWorldType.CustomFlat(materials);
            } else if (args.get(2).equalsIgnoreCase("custom_height")) {
                if (!args.isLengthEquals(4, true)) {
                    return;
                }

                String materialsString = args.getString(3);
                List<Tuple<Integer, Material>> materials = this.parseMaterials(sender,
                        materialsString);
                if (materials == null) {
                    return;
                }

                double scale = args.getOptionOrElse("scale", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.SCALE))
                        .toBoundedDoubleOrExit(0, 1, true);

                double xScale = args.getOptionOrElse("xScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double yScale = args.getOptionOrElse("yScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double zScale = args.getOptionOrElse("zScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double frequency = args.getOptionOrElse("frequency", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.FREQUENCY))
                        .toBoundedDoubleOrExit(0, 1, true);

                double amplitude = args.getOptionOrElse("amplitude", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.AMPLITUDE))
                        .toBoundedDoubleOrExit(0, 1, true);

                int baseHeight = args.getOptionOrElse("baseHeight", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.BASE_HEIGHT))
                        .toBoundedIntOrExit(-64, 320, true);

                boolean simplex = args.containsFlag('s');

                worldType = new ExWorldType.CustomHeight(simplex, xScale, yScale, zScale,
                        frequency, amplitude, baseHeight, materials);
            } else if (args.get(2).equalsIgnoreCase("custom_island")) {
                if (!args.isLengthEquals(4, true)) {
                    return;
                }

                String materialsString = args.getString(3);
                List<Tuple<Integer, Material>> materials = this.parseMaterials(sender,
                        materialsString);
                if (materials == null) {
                    return;
                }

                float density = args.getOptionOrElse("density", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.DENSITY))
                        .toBoundedFloatOrExit(0, 1, true);

                double scale = args.getOptionOrElse("scale", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.SCALE))
                        .toBoundedDoubleOrExit(0, 1, true);

                double xScale = args.getOptionOrElse("xScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double yScale = args.getOptionOrElse("yScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double zScale = args.getOptionOrElse("zScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double frequency = args.getOptionOrElse("frequency", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.FREQUENCY))
                        .toBoundedDoubleOrExit(0, 1, true);

                double amplitude = args.getOptionOrElse("amplitude", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.AMPLITUDE))
                        .toBoundedDoubleOrExit(0, 1, true);

                worldType = new ExWorldType.CustomIsland(density, xScale, yScale, zScale,
                        frequency, amplitude,
                        materials);
            } else if (args.get(2).equalsIgnoreCase("custom_island")) {
                if (!args.isLengthEquals(4, true)) {
                    return;
                }

                String materialsString = args.getString(3);
                List<Tuple<Integer, Material>> materials = this.parseMaterials(sender,
                        materialsString);
                if (materials == null) {
                    return;
                }

                float density = args.getOptionOrElse("density", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.DENSITY))
                        .toBoundedFloatOrExit(0, 1, true);

                double scale = args.getOptionOrElse("scale", new CmdOption(sender,
                                "" + ExWorldType.CustomHeight.SCALE))
                        .toBoundedDoubleOrExit(0, 1, true);

                double xScale = args.getOptionOrElse("xScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double yScale = args.getOptionOrElse("yScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double zScale = args.getOptionOrElse("zScale", new CmdOption(sender,
                                "" + scale))
                        .toBoundedDoubleOrExit(0, 1, true);

                double frequency = args.getOptionOrElse("frequency", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.FREQUENCY))
                        .toBoundedDoubleOrExit(0, 1, true);

                double amplitude = args.getOptionOrElse("amplitude", new CmdOption(sender,
                                "" + ExWorldType.CustomIsland.AMPLITUDE))
                        .toBoundedDoubleOrExit(0, 1, true);

                worldType = new ExWorldType.CustomIsland(density, xScale, yScale, zScale,
                        frequency, amplitude, materials);
            }
        }

        boolean temporary = args.containsFlag('t');

        sender.sendPluginMessage(
                Component.text("Creating " + (temporary ? "temporary " : "") + "world ",
                                ExTextColor.PERSONAL)
                        .append(Component.text(worldName, ExTextColor.VALUE,
                                        TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(
                                        Component.text("Click to teleport to world")))
                                .clickEvent(ClickEvent.runCommand("/mw tp " + worldName)))
                        .append(Component.text(" with type ", ExTextColor.PERSONAL))
                        .append(Component.text(worldType.getName(), ExTextColor.VALUE)));

        ExWorld createdWorld = Server.getWorldManager()
                .createWorld(worldName, worldType, temporary);

        if (createdWorld != null) {
            this.waitingWorldLoadedSenderByWorldName.put(createdWorld.getName(), sender);
        } else {
            sender.sendPluginTDMessage("§wFailed to load world " + worldName);
        }
    }

    private List<Tuple<Integer, Material>> parseMaterials(Sender sender, String arg) {
        List<Tuple<Integer, Material>> materials = new LinkedList<>();
        String[] materialStrings = arg.split(",");

        for (String heightMaterialString : materialStrings) {
            String[] heightMaterial = heightMaterialString.split("#");
            if (heightMaterial.length == 1) {
                Material material;
                try {
                    material = Material.valueOf(heightMaterial[0].toUpperCase());
                } catch (IllegalArgumentException var21) {
                    sender.sendMessageNoItemName(heightMaterial[0].toUpperCase());
                    return null;
                }

                materials.add(new Tuple<>(1, material));
            } else {
                if (heightMaterial.length != 2) {
                    sender.sendPluginTDMessage("§wInvalid layer §v" + heightMaterialString);
                    return null;
                }

                Material material;
                int height;
                try {
                    height = Integer.parseInt(heightMaterial[0]);
                    material = Material.valueOf(heightMaterial[1].toUpperCase());
                } catch (NumberFormatException var19) {
                    sender.sendMessageNoInteger(heightMaterial[0]);
                    return null;
                } catch (IllegalArgumentException var20) {
                    sender.sendMessageNoItemName(heightMaterial[1].toUpperCase());
                    return null;
                }

                materials.add(new Tuple<>(height, material));
            }
        }

        return materials;
    }

    private void sendCmdMessages(Sender sender) {
        if (sender.hasPermission("exbukkit.world.create")) {
            sender.sendTDMessageCommandHelp("Create a new world", "mw create <world> [type]");
        }
        if (sender.hasPermission("exbukkit.world.delete")) {
            sender.sendTDMessageCommandHelp("Delete an existing world", "mw delete <world>");
        }
        if (sender.hasPermission("exbukkit.world.unload")) {
            sender.sendTDMessageCommandHelp("Unload an existing world", "mw unload <world>");
        }
        if (sender.hasPermission("exbukkit.world.teleport")) {
            sender.sendTDMessageCommandHelp("Teleport to a world", "mw tp <world> [player]");
        }
        if (sender.hasPermission("exbukkit.world.clone")) {
            sender.sendTDMessageCommandHelp("Clone a world", "mw clone <source> <world>");
        }
        if (sender.hasPermission("exbukkit.world.rename")) {
            sender.sendTDMessageCommandHelp("Rename a world", "mw rename <world> <newName>");
        }
        if (sender.hasPermission("exbukkit.world.list")) {
            sender.sendTDMessageCommandHelp("List the worlds", "mw list");
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd,
            ExArguments<Argument> args) {
        if (args.getLength() >= 1) {
            if (args.getLength() == 1) {
                return List.of("create", "tp", "delete", "clone", "list", "unload", "rename");
            }
            switch (args.getString(0).toLowerCase()) {
                case "create" -> {
                    if (args.length() == 2) {
                        return List.of("<name>");
                    }

                    if (args.getLength() == 3) {
                        return Stream.concat(ExWorldType.getNames().stream(),
                                        Stream.of("custom_height", "custom_flat", "custom_island"))
                                .collect(Collectors.toList());
                    }

                    if (args.length() == 4 && args.getOptions().isEmpty() && args.getFlags()
                            .isEmpty()
                            && args.get(2)
                            .equalsIgnoreCase("custom_height", "custom_flat", "custom_island")) {

                        List<String> completion = new LinkedList<>();

                        List<String> numbers = List.of("1#", "2#", "3#", "10#", "16#", "64#",
                                "128#");

                        String materialString = args.getString(3);
                        if (materialString.isEmpty() || materialString.endsWith(",")) {
                            completion.addAll(
                                    numbers.stream().map(n -> materialString + n).toList());
                            completion.addAll(Arrays.stream(Material.values())
                                    .map(m -> m.name().toLowerCase()).toList());
                        } else if (materialString.endsWith("#")) {
                            completion.addAll(Arrays.stream(Material.values())
                                    .map(m -> materialString + m.name().toLowerCase()).toList());
                        } else {
                            if (materialString.contains("#")
                                    && !Character.isDigit(
                                    materialString.charAt(materialString.length() - 1))) {
                                String materialName = materialString.substring(
                                                materialString.lastIndexOf('#'))
                                        .replace("#", "");

                                completion.addAll(Arrays.stream(Material.values())
                                        .filter(m -> m.name().toLowerCase().startsWith(materialName)
                                                && !m.name().equalsIgnoreCase(materialString))
                                        .map(m -> materialString + m.name().toLowerCase())
                                        .toList());

                                if (Material.getMaterial(materialName.toUpperCase()) != null) {
                                    completion.add(materialString + ",");
                                }
                            } else {
                                completion.add(materialString + "#");
                            }
                        }
                        return completion;
                    }

                    if (args.length() > 4 && args.get(2).equalsIgnoreCase("custom_height")) {
                        return List.of("--scale=", "--xScale=", "--yScale=", "--zScale=",
                                "--frequency=", "--amplitude=", "-s");
                    }

                    if (args.length() > 4 && args.get(2).equalsIgnoreCase("custom_island")) {
                        return List.of("--scale=", "--xScale=", "--yScale=", "--zScale=",
                                "--frequency=", "--amplitude=", "--density=");
                    }
                }
                case "tp", "teleport" -> {
                    if (args.getLength() == 2) {
                        return Server.getCommandManager().getTabCompleter().getWorldNames();
                    }
                    if (args.getLength() == 3) {
                        return Server.getCommandManager().getTabCompleter().getPlayerNames();
                    }
                }
                case "delete", "unload", "clone", "rename" -> {
                    if (args.getLength() == 2) {
                        return Server.getCommandManager().getTabCompleter().getWorldNames();
                    }
                }
            }
        }
        return List.of();
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.listPerm = plugin.createPermssionCode("exbukkit.world.list");
        this.createPerm = plugin.createPermssionCode("exbukkit.world.create");
        this.clonePerm = plugin.createPermssionCode("exbukkit.world.clone");
        this.deletePerm = plugin.createPermssionCode("exbukkit.world.delete");
        this.unloadPerm = plugin.createPermssionCode("exbukkit.world.unload");
        this.teleportPerm = plugin.createPermssionCode("exbukkit.world.teleport");
        this.renamePerm = plugin.createPermssionCode("exbukkit.world.rename");
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        Sender sender = this.waitingWorldLoadedSenderByWorldName.remove(e.getWorld().getName());
        if (sender != null) {
            sender.sendPluginMessage(Component.text("Complete", ExTextColor.PERSONAL));
        }
    }
}
