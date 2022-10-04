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
import org.bukkit.GameMode;

import java.util.List;

public class CmdGamemode implements CommandListener {

    private Code.Permission perm;
    private Code.Permission otherPerm;

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

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.perm = plugin.createPermssionCode("gmo", "exbukkit.gamemode");
        this.otherPerm = plugin.createPermssionCode("gmo", "exbukkit.gamemode.other");
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
            case "survival", "0" -> {
                gameMode = GameMode.SURVIVAL;
                name = "Survival";
            }
            case "creative", "1" -> {
                gameMode = GameMode.CREATIVE;
                name = "Creative";
            }
            case "adventure", "2" -> {
                gameMode = GameMode.ADVENTURE;
                name = "Adventure";
            }
            case "spectator", "3" -> {
                gameMode = GameMode.SPECTATOR;
                name = "Spectator";
            }
            default -> {
                sender.sendMessageGamemodeNotExist(args.get(0).getString());
                return;
            }
        }


        if (sender.getName().equals(user.getName())) {
            if (!sender.hasPermission(this.perm)) {
                return;
            }

        } else {
            if (!sender.hasPermission(this.otherPerm)) {
                return;
            }

            if (!sender.hasGroupRankLower(sender.getUser().getUniqueId())) {
                return;
            }
            sender.sendPluginMessage(Component.text("Updated gamemode from ", ExTextColor.PERSONAL)
                    .append(user.getChatNameComponent())
                    .append(Component.text(" to ", ExTextColor.PERSONAL))
                    .append(Component.text(name, ExTextColor.VALUE)));
        }

        user.setGameMode(gameMode);
        user.sendPluginMessage(Plugin.BUKKIT, Component.text("Updated gamemode to ", ExTextColor.PERSONAL)
                .append(Component.text(name, ExTextColor.VALUE)));

    }
}
