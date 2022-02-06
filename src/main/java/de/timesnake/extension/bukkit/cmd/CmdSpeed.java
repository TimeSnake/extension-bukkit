package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.cmd.Arguments;
import de.timesnake.library.basic.util.cmd.ExCommand;

import java.util.List;

public class CmdSpeed implements CommandListener {

    enum Type {
        FLY, WALK
    }

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

    private void set(Sender sender, User user, float speed, Type mode) {
        if (sender.getName().equals(user.getName())) {
            if (!sender.hasPermission("exbukkit.speed." + mode, 915)) {
                return;
            }

            if (setWithPermission(sender, user, speed, mode)) {
                return;
            }

            user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Updated " + mode + "speed to " + ChatColor.VALUE + speed);
        } else {
            if (sender.hasPermission("exbukkit.speed." + mode + ".other", 917)) {
                if (setWithPermission(sender, user, speed, mode)) {
                    return;
                }

                sender.sendPluginMessage(ChatColor.PERSONAL + "Updated " + mode + "speed from " + ChatColor.VALUE + user.getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + speed);
                user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Updated " + mode + "speed by " + ChatColor.VALUE + sender.getChatName() + ChatColor.PERSONAL + " to " + ChatColor.VALUE + speed);
            }

        }
    }

    private boolean setWithPermission(Sender sender, User user, float speed, Type mode) {
        if (!(speed <= 5 || speed > 0)) {
            sender.sendPluginMessage(ChatColor.WARNING + "Top speed is 5");
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
}
