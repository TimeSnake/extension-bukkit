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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class CmdInventory implements CommandListener, Listener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        switch (cmd.getName().toLowerCase()) {
            case "inventory":
            case "inv":
            case "invsee":
                if (args.isLengthEquals(1, true)) {
                    this.see(sender, args.get(0));
                } else {
                    sender.sendMessageCommandHelp("See inventory form player", "invsee <player>");
                }
                break;
            case "clear":
            case "invclear":
                if (args.isLengthEquals(1, false)) {
                    this.clear(sender, args.get(0));
                } else if (sender.isPlayer(true)) {
                    this.clear(sender, new Argument(sender, sender.getPlayer().getName()));
                }
                break;
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return Server.getCommandManager().getTabCompleter().getPlayerNames();
        }
        return List.of();
    }

    public void see(Sender sender, Argument arg) {
        if (!sender.hasPermission("exbukkit.inventory.see", 928)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        if (!arg.isPlayerName(true)) {
            return;
        }

        Player p = sender.getPlayer();

        Inventory inv = arg.toPlayer().getInventory();
        p.openInventory(inv);
    }

    public void clear(Sender sender, Argument arg) {
        if (!sender.hasPermission("exbukkit.inventory.clear", 930)) {
            return;
        }

        if (!arg.isPlayerName(true)) {
            return;
        }

        User user = arg.toUser();
        user.clearInventory();
        sender.sendPluginMessage(ChatColor.PERSONAL + "Cleared inventory from " + ChatColor.VALUE + user.getChatName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        org.bukkit.inventory.Inventory inv = e.getInventory();
        if (e.getView().getTitle().equals("Inventory")) {
            if (inv.getHolder() != null) {
                if (!inv.getHolder().equals(e.getWhoClicked())) {
                    Sender sender = Server.getUser((Player) e.getWhoClicked()).asSender(Plugin.BUKKIT);
                    if (!sender.hasPermission("exbukkit.inventory.modify", 929)) {
                        e.setCancelled(true);
                    }
                    e.setCancelled(false);
                }
            }
        }
    }

}
