/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class CmdChatCopy implements CommandListener {

    private Code.Permission perm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!sender.hasPermission(this.perm)) {
            return;
        }

        if (args.isLengthEquals(0, true)) {
            sender.sendMessageCommandHelp("Send copyable message", "cc <message>");
            return;
        }

        String msg = args.toMessage();

        Server.broadcastClickableMessage(Server.getChatManager().getSender(sender).append(
                        LegacyComponentSerializer.legacyAmpersand().deserialize(msg).decorate(TextDecoration.UNDERLINED)),
                msg, Component.text("Click to copy to clipboard"), net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD);
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }

    @Override
    public void loadCodes(Plugin plugin) {
        this.perm = plugin.createPermssionCode("ccc", "exbukkit.chatcopy");
    }
}
