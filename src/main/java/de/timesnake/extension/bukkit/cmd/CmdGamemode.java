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
import org.bukkit.GameMode;

import java.util.List;

public class CmdGamemode implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        this.handleCmdGamemode(sender, args);
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return List.of("survival", "creative", "adventure", "spectator");
        } else if (args.getLength() == 2) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return null;
    }

    public void handleCmdGamemode(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            sender.sendMessageCommandHelp("Set gamemode", "gm <mode>");
            sender.sendMessageCommandHelp("Set gamemode for player", "gm <mode> <player>");
            return;
        }

        User user = sender.getUser();
        if (args.isLengthEquals(2, false)) {
            if (args.get(1).isPlayerName(true)) {
                user = args.get(1).toUser();
            } else {
                return;
            }
        }

        String name;
        GameMode gameMode;

        switch (args.get(0).toLowerCase()) {

            case "survival":
            case "0":
                gameMode = GameMode.SURVIVAL;
                name = "Survival";
                break;

            case "creative":
            case "1":
                gameMode = GameMode.CREATIVE;
                name = "Creative";
                break;

            case "adventure":
            case "2":
                gameMode = GameMode.ADVENTURE;
                name = "Adventure";
                break;

            case "spectator":
            case "3":
                gameMode = GameMode.SPECTATOR;
                name = "Spectator";

                break;
            default:
                sender.sendMessageGamemodeNotExist(args.get(0).getString());
                return;
        }


        if (sender.getName().equals(user.getName())) {
            if (!sender.hasPermission("exbukkit.gamemode", 913)) {
                return;
            }

        } else {
            if (!sender.hasPermission("exbukkit.gamemode.other", 914)) {
                return;
            }

            if (!sender.hasGroupRankLower(sender.getUser().getUniqueId())) {
                return;
            }
            sender.sendPluginMessage(ChatColor.PERSONAL + "Updated gamemode from " + ChatColor.VALUE + user.getChatName() + " to " + ChatColor.VALUE + name);
        }

        user.setGameMode(gameMode);
        user.sendPluginMessage(Plugin.BUKKIT, ChatColor.PERSONAL + "Updated gamemode to " + ChatColor.VALUE + name);

    }
}
