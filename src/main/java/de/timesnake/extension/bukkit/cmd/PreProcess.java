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

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.LinkedList;

public class PreProcess implements Listener {

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

        String[] message = e.getMessage().split(" ");
        Sender sender = Server.getUser(e.getPlayer()).asSender(Plugin.BUKKIT);
        switch (message[0]) {
            case "time":
                e.setCancelled(true);
                new CmdTime().handleCmdTime(sender, new Arguments<>(sender, PreProcess.getArgs(e)));
                break;
            case "gamemode":
                e.setCancelled(true);
                new CmdGamemode().handleCmdGamemode(sender, new Arguments<>(sender, PreProcess.getArgs(e)));
            case "weather":
                e.setCancelled(true);
                new CmdWeather().handleCmdWeather(sender, new Arguments<>(sender, PreProcess.getArgs(e)));
            case "kill":
                e.setCancelled(true);
                new CmdKill().killPlayer(sender, new Arguments<>(sender, PreProcess.getArgs(e)));
        }
    }

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


}
