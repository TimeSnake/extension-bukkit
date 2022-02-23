package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.List;

public class CmdChatCopy implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!sender.hasPermission("exbukkit.chatcopy", 955)) {
            return;
        }

        if (args.isLengthEquals(0, true)) {
            sender.sendMessageCommandHelp("Send copyable message", "cc <message>");
            return;
        }

        for (User user : Server.getUsers()) {
            TextComponent tc = new TextComponent();
            tc.setText(Server.getChatManager().getSender(sender) + args.toMessage());
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, args.toMessage()));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy to clipboard")));
            user.sendMessage(tc);

            Server.sendChatMessageToConsole(Server.getChatManager().getSender(sender) + args.toMessage());
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }
}
