/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.ExcludedInventoryHolder;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CmdInventory implements CommandListener, Listener {

    private static final Map<Integer, Integer> PLAYER_SEE_INV = new HashMap<>();

    private static final ExItemStack COMMIT = new ExItemStack(Material.RED_DYE, "§cCommit").setSlot(8);

    static {
        PLAYER_SEE_INV.put(36, 0); // feet
        PLAYER_SEE_INV.put(37, 1); // legs
        PLAYER_SEE_INV.put(38, 2); // chest
        PLAYER_SEE_INV.put(39, 3); // head
        PLAYER_SEE_INV.put(40, 5); // off-hand
    }

    private Code.Permission seePerm;
    private Code.Permission modifyPerm;
    private Code.Permission clearPerm;

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

    @Override
    public void loadCodes(de.timesnake.library.extension.util.chat.Plugin plugin) {
        this.seePerm = plugin.createPermssionCode("inv", "exbukkit.inventory.see");
        this.modifyPerm = plugin.createPermssionCode("inv", "exbukkit.inventory.modify");
        this.clearPerm = plugin.createPermssionCode("inv", "exbukkit.inventory.clear");
    }

    public void see(Sender sender, Argument arg) {
        if (!sender.hasPermission(this.seePerm)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        if (!arg.isPlayerName(true)) {
            return;
        }

        Player p = sender.getPlayer();

        User seeUser = arg.toUser();

        ItemStack[] items = seeUser.getInventory().getContents();

        VirtuellInventoryHolder holder = new VirtuellInventoryHolder(seeUser);
        Inventory inv = new ExInventory(6 * 9, Component.text(seeUser.getName()), holder).getInventory();
        holder.setInventory(inv);

        for (int slot = 0; slot < 41; slot++) {
            ItemStack item = items[slot];

            if (PLAYER_SEE_INV.containsKey(slot)) {
                inv.setItem(PLAYER_SEE_INV.get(slot), item);
            } else {
                inv.setItem(slot + 18, item);
            }
        }

        inv.setItem(COMMIT.getSlot(), COMMIT);

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getView().getTopInventory().getHolder();

        if (!(holder instanceof VirtuellInventoryHolder vHolder)) {
            return;
        }

        User seeUser = vHolder.getUser();

        Sender sender = Server.getUser((Player) e.getWhoClicked()).asSender(Plugin.BUKKIT);
        if (!sender.hasPermission(this.modifyPerm)) {
            e.setCancelled(true);
        }

        ItemStack clickedItem = e.getCurrentItem();

        if (COMMIT.equals(ExItemStack.getItem(clickedItem, false))) {
            e.setCancelled(true);

            ItemStack[] items = e.getView().getTopInventory().getContents();

            Inventory userInv = seeUser.getInventory();

            userInv.clear();

            for (Map.Entry<Integer, Integer> entry : PLAYER_SEE_INV.entrySet()) {
                ItemStack item = items[entry.getValue()];

                if (item == null) {
                    continue;
                }

                userInv.setItem(entry.getKey(), item);
            }

            for (int slot = 18; slot < 41; slot++) {
                ItemStack item = items[slot];

                if (item == null) {
                    continue;
                }

                userInv.setItem(slot - 18, item);
            }

            seeUser.updateInventory();

            return;
        }

    }

    public void clear(Sender sender, Argument arg) {
        if (!sender.hasPermission(this.clearPerm)) {
            return;
        }

        if (!arg.isPlayerName(true)) {
            return;
        }

        User user = arg.toUser();
        user.clearInventory();
        sender.sendPluginMessage(Component.text("Cleared inventory from ", ExTextColor.PERSONAL)
                .append(user.getChatNameComponent()));
    }

    private static class VirtuellInventoryHolder implements ExcludedInventoryHolder {

        private final User user;
        private Inventory inventory;

        public VirtuellInventoryHolder(User user) {
            this.user = user;
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VirtuellInventoryHolder that = (VirtuellInventoryHolder) o;
            return Objects.equals(inventory, that.inventory);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inventory);
        }

        public User getUser() {
            return user;
        }
    }

}
