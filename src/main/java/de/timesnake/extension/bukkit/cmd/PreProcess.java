/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.core.chat.CommandManager;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.extension.bukkit.chat.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.LinkedList;

public class PreProcess implements Listener {

    private static LinkedList<Argument> getArgs(PlayerCommandPreprocessEvent e) {
        int i = 0;
        LinkedList<Argument> args = new LinkedList<>();
        Sender sender = Server.getUser(e.getPlayer()).asSender(Plugin.BUKKIT);
        for (String arg : e.getMessage().split(" ")) {
            if (i == 0) {
                continue;
            }
            args.add(new Argument(sender, arg));
            i++;
        }
        return args;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

        String[] message = e.getMessage().split(" ");
        Sender sender = Server.getUser(e.getPlayer()).asSender(Plugin.BUKKIT);
        switch (message[0]) {
            case "time":
                e.setCancelled(true);
                new CmdTime().handleCmdTime(sender, new CommandManager.Arguments(sender, PreProcess.getArgs(e)));
                break;
            case "gamemode":
                e.setCancelled(true);
                new CmdGamemode().handleCmdGamemode(sender, new CommandManager.Arguments(sender, PreProcess.getArgs(e)));
            case "weather":
                e.setCancelled(true);
                new CmdWeather().handleCmdWeather(sender, new CommandManager.Arguments(sender, PreProcess.getArgs(e)));
            case "kill":
                e.setCancelled(true);
                new CmdKill().killPlayer(sender, new CommandManager.Arguments(sender, PreProcess.getArgs(e)));
        }
    }


}
