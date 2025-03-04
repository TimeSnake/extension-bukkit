/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CmdChatCopy implements CommandListener {

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.chatcopy");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (!sender.hasPermission(this.perm)) {
      return;
    }

    if (args.isLengthEquals(0, true)) {
      sender.sendTDMessageCommandHelp("Send copyable message", "cc <message>");
      return;
    }

    String msg = args.toMessage();

    Server.broadcastClickableMessage(Server.getChatManager().getSender(sender).append(
            LegacyComponentSerializer.legacyAmpersand().deserialize(msg).decorate(TextDecoration.UNDERLINED)),
        msg, Component.text("Click to copy to clipboard"),
        net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD);
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm);
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
