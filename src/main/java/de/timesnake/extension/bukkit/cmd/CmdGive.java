/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import java.util.List;

public class CmdGive implements CommandListener {

  @Override
  public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd,
      Arguments<Argument> args) {
    if (args.isLengthHigherEquals(1, true)) {
      if (args.get(0).isPlayerName(false)) {
        if (args.isLengthHigherEquals(2, false)) {
        } else {

        }
      } else {

      }
    } else {
      sender.sendTDMessageCommandHelp("Give a item to a player",
          "give [Player] <item> [amount]");
    }

  }

  @Override
  public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd,
      Arguments<Argument> args) {
    return null;
  }

  @Override
  public void loadCodes(Plugin plugin) {

  }

}
