/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.commands.simple.Arguments;
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
        i++;
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
        new CmdTime().handleCmdTime(sender, new Arguments<>(sender, PreProcess.getArgs(e)) {
          @Override
          public Argument createArgument(de.timesnake.library.commands.Sender sender, String arg) {
            return new Argument(((Sender) sender), arg);
          }
        });
        break;
      case "gamemode":
        e.setCancelled(true);
        new CmdGamemode().handleCmdGamemode(sender, new Arguments<>(sender, PreProcess.getArgs(e)) {
          @Override
          public Argument createArgument(de.timesnake.library.commands.Sender sender, String arg) {
            return new Argument(((Sender) sender), arg);
          }
        });
      case "weather":
        e.setCancelled(true);
        new CmdWeather().handleCmdWeather(sender, new Arguments<>(sender, PreProcess.getArgs(e)) {
          @Override
          public Argument createArgument(de.timesnake.library.commands.Sender sender, String arg) {
            return new Argument(((Sender) sender), arg);
          }
        });
      case "kill":
        e.setCancelled(true);
        new CmdKill().killPlayer(sender, new Arguments<>(sender, PreProcess.getArgs(e)) {
          @Override
          public Argument createArgument(de.timesnake.library.commands.Sender sender, String arg) {
            return new Argument(((Sender) sender), arg);
          }
        });
    }
  }


}
