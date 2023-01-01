/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;

import java.util.List;

public class CmdSpeed implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        switch (cmd.getName().toLowerCase()) {
            case "speed":
                if (args.isLengthHigherEquals(1, true)) {
                    if (args.get(0).isFloat(true)) {
                        User user = sender.getUser();
                        if (args.isLengthEquals(2, false)) {
                            if (args.get(1).isPlayerName(true)) {
                                user = args.get(1).toUser();
                            }
                        }
                        if (user.getPlayer().isFlying()) {
                            this.set(sender, user, args.get(0).toFloat(), Type.FLY);
                        } else {
                            this.set(sender, user, args.get(0).toFloat(), Type.WALK);
                        }
                    }
                } else {
                    sender.sendMessageCommandHelp("Set speed", "speed <speed>");
                    sender.sendMessageCommandHelp("Set speed for player", "speed <speed> <player>");
                }
                break;

            case "speedfly":
                if (args.isLengthHigherEquals(1, true)) {
                    if (args.get(0).isFloat(true)) {
                        User user = sender.getUser();
                        if (args.isLengthEquals(2, false)) {
                            if (args.get(1).isPlayerName(true)) {
                                user = args.get(1).toUser();
                            }
                        }
                        this.set(sender, user, args.get(0).toFloat(), Type.FLY);
                    }
                } else {
                    sender.sendMessageCommandHelp("Set fly speed", "speedfly <speed>");
                    sender.sendMessageCommandHelp("Set fly speed for player", "speedfly <speed> <player>");
                }
                break;

            case "speedwalk":
                if (args.isLengthHigherEquals(1, true)) {
                    if (args.get(0).isFloat(true)) {
                        User user = sender.getUser();
                        if (args.isLengthEquals(2, false)) {
                            if (args.get(1).isPlayerName(true)) {
                                user = args.get(1).toUser();
                            }
                        }
                        this.set(sender, user, args.get(0).toFloat(), Type.WALK);

                    }
                } else {
                    sender.sendMessageCommandHelp("Set walk speed", "speedwalk <speed>");
                    sender.sendMessageCommandHelp("Set walk speed for player", "speedwalk <speed> <player>");
                }
                break;
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return List.of("1", "2", "1.2", "0.5");
        } else if (args.getLength() == 2) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return null;
    }

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("spe", "exbukkit.speed.<mode>");
        this.otherPerm = plugin.createPermssionCode("spe", "exbukkit.speed.<mode>.other");
    }

    private void set(Sender sender, User user, float speed, Type mode) {
        if (sender.getName().equals(user.getName())) {
            if (!sender.hasPermission(this.perm)) {
                return;
            }

            if (setWithPermission(sender, user, speed, mode)) {
                return;
            }

            user.sendPluginMessage(Plugin.BUKKIT, Component.text("Updated " + mode.name().toLowerCase() +
                    "speed to ", ExTextColor.PERSONAL).append(Component.text(speed, ExTextColor.VALUE)));
        } else {
            if (sender.hasPermission(this.otherPerm)) {
                if (setWithPermission(sender, user, speed, mode)) {
                    return;
                }

                sender.sendPluginMessage(Component.text("Updated " + mode.name().toLowerCase() + "speed from ", ExTextColor.PERSONAL)
                        .append(user.getChatNameComponent())
                        .append(Component.text(" to ", ExTextColor.PERSONAL))
                        .append(Component.text(speed, ExTextColor.VALUE)));
                user.sendPluginMessage(Plugin.BUKKIT, Component.text("Updated " + mode.name().toLowerCase() +
                                "speed by ", ExTextColor.PERSONAL)
                        .append(sender.getChatName())
                        .append(Component.text(" to ", ExTextColor.PERSONAL))
                        .append(Component.text(speed, ExTextColor.VALUE)));
            }

        }
    }

    private boolean setWithPermission(Sender sender, User user, float speed, Type mode) {
        if (!(speed <= 5 || speed > 0)) {
            sender.sendPluginMessage(Component.text("Top speed is 5", ExTextColor.WARNING));
            return true;
        }

        if (mode == null) {
            mode = Type.WALK;
        }

        if (mode.equals(Type.FLY)) {
            user.getPlayer().setFlySpeed((float) (speed * 0.2));
        } else {
            user.getPlayer().setWalkSpeed((float) (speed * 0.2));
        }

        return false;
    }

    enum Type {
        FLY,
        WALK
    }
}
