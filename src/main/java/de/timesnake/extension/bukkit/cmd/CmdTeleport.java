package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class CmdTeleport implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        switch (cmd.getName().toLowerCase()) {
            case "tp":
            case "teleport":
                Teleport.teleport(sender, args);
                break;

            case "tph":
            case "tphere":
            case "teleporthere":
                Teleport.teleportHere(sender, args);
                break;
            //tpa
            case "tpa":
            case "call":
            case "tpask":
            case "teleportask":
                Teleport.teleportAsk(sender, args);
                break;
            //tpahere
            case "tpahere":
            case "tpaskhere":
            case "tpah":
            case "tpaskh":
            case "teleportaskhere":
                Teleport.teleportHereAsk(sender, args);
                break;
            //tpa settings
            case "tpaccept":
                Teleport.accept(sender, args);
                break;

            case "tpdeny":
                Teleport.deny(sender, args);
                break;

            //spawn
            case "spawn":
                Teleport.teleportSpawn(sender);
                break;

            case "setspawn":
                Teleport.setSpawn(sender, args);
                break;

            case "back":
                Teleport.back(sender, args);
                break;
        }

    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        List<String> players = Server.getCommandManager().getTabCompleter().getPlayerNames();
        switch (cmd.getName().toLowerCase()) {
            case "tp":
            case "teleport":
                switch (args.getLength()) {
                    case 1:
                        players.addAll(List.of("~", "0"));
                        return players;
                    case 2:
                        if (args.get(0).isPlayerName(false)) {
                            return players;
                        } else {
                            return List.of("~", "0");
                        }
                    case 3:
                        if (!args.get(0).isPlayerName(false)) {
                            return List.of("~");
                        }
                }
                break;
            case "tph":
            case "tphere":
            case "teleporthere":
            case "tpahere":
            case "tpaskhere":
            case "tpah":
            case "tpaskh":
            case "tpa":
            case "call":
            case "tpask":
            case "teleportask":
                if (args.getLength() == 1) {
                    return players;
                }
                break;
            case "setspawn":
                switch (args.getLength()) {
                    case 1:
                    case 2:
                    case 3:
                        return List.of("~");
                }
                break;
        }
        return null;
    }

}
