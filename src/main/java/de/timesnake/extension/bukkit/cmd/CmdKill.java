package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CmdKill implements CommandListener {

    private final List<EntityType> monsters = List.of(EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER,
            EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.GHAST, EntityType.GIANT, EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.MAGMA_CUBE, EntityType.PHANTOM,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.PIGLIN, EntityType.PILLAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON,
            EntityType.SLIME, EntityType.SPIDER, EntityType.VEX, EntityType.WITCH, EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.VINDICATOR, EntityType.EVOKER,
            EntityType.EVOKER_FANGS);

    private final List<EntityType> excluded = List.of(EntityType.ARMOR_STAND, EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING, EntityType.BOAT, EntityType.BOAT, EntityType.MINECART, EntityType.MINECART_FURNACE,
            EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND, EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT);

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (cmd.getName().equalsIgnoreCase("kill")) {
            this.killPlayer(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("killall")) {
            if (args.isLengthEquals(1, false)) {
                this.killType(sender, args.get(0));
            } else if (args.isLengthEquals(0, true)) {
                if (sender.isPlayer(true)) {
                    this.killTypeAll(sender);
                }
            } else {
                sender.sendMessageCommandHelp("Kill all of a type (drops, mobs, ...)", "killall <type>");
                sender.sendMessageCommandHelp("Kill all types", "killall all");
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            List<String> players = Server.getCommandManager().getTabCompleter().getPlayerNames();
            players.addAll(List.of("drops", "mobs", "monsters", "animals", "xps", "all", "<mobType>"));
            return players;
        }
        return null;

    }

    public void killPlayer(Sender sender, Arguments<Argument> args) {
        if (!args.isLengthEquals(1, true)) {
            sender.sendMessageCommandHelp("Kill a player", "kill <player>");
            return;
        }

        if (!sender.hasPermission("exbukkit.kill.player", 940)) {
            return;
        }

        if (!args.get(0).isPlayerName(true)) {
            return;
        }

        User user = args.get(0).toUser();
        if (!sender.hasGroupRankLowerEquals(user)) {
            return;
        }

        user.getPlayer().setHealth(0);
    }

    public void killType(Sender sender, Argument arg) {
        if (!sender.hasPermission("exbukkit.kill.type", 941)) {
            return;
        }

        World w = sender.getUser().getPlayer().getWorld();
        int i = 0;

        switch (arg.toLowerCase()) {
            case "drops", "drop" -> {
                for (Entity entity : w.getEntities()) {
                    if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                        entity.remove();
                        i++;
                    }
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " drops from world " + ChatColor.VALUE + w.getName());
            }
            case "mobs" -> {
                for (Entity entity : w.getLivingEntities()) {
                    if (!entity.getType().equals(EntityType.PLAYER)) {
                        entity.remove();
                        i++;
                    }
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " mobs from world " + ChatColor.VALUE + w.getName());
            }
            case "monsters", "monster" -> {
                for (Entity entity : w.getLivingEntities()) {
                    if (this.monsters.contains(entity.getType())) {
                        entity.remove();
                        i++;
                    }
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " monsters from world " + ChatColor.VALUE + w.getName());
            }
            case "xps", "xp" -> {
                for (Entity entity : w.getEntities()) {
                    if (entity.getType().equals(EntityType.THROWN_EXP_BOTTLE) || entity.getType().equals(EntityType.EXPERIENCE_ORB)) {
                        entity.remove();
                        i++;
                    }
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " xps from world " + ChatColor.VALUE + w.getName());
            }
            case "animals", "animal" -> {
                for (Entity entity : w.getLivingEntities()) {
                    if (!entity.getType().equals(EntityType.PLAYER) && !this.monsters.contains(entity.getType())) {
                        entity.remove();
                        i++;
                    }
                }
                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " animals from world " + ChatColor.VALUE + w.getName());
            }
            case "all" -> this.killTypeAll(sender);
            default -> {
                EntityType type;
                try {
                    type = EntityType.valueOf(arg.toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessageCommandHelp("Kill all of a type (drops, mobs, ...)", "killall <type>");
                    sender.sendMessageCommandHelp("Kill all types", "killall all");
                    sender.sendMessageKillAllTypeNotExist(arg.getString());
                    return;
                }

                for (Entity entity : w.getEntities()) {
                    if (entity.getType().equals(type)) {
                        entity.remove();
                        i++;
                    }
                }

                sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL +
                        " " + arg.toLowerCase() + " from world " + ChatColor.VALUE + w.getName());
            }
        }
    }

    public void killTypeAll(Sender sender) {
        if (!sender.hasPermission("exbukkit.kill.all", 942)) {
            return;
        }

        World w = sender.getUser().getPlayer().getWorld();
        int i = 0;
        for (Entity entity : w.getEntities()) {
            if (!entity.getType().equals(EntityType.PLAYER) && !this.excluded.contains(entity.getType())) {
                entity.remove();
                i++;
            }
        }
        sender.sendPluginMessage(ChatColor.PERSONAL + "Removed " + ChatColor.VALUE + i + ChatColor.PERSONAL + " " +
                "entities from" + " world " + ChatColor.VALUE + w.getName());
    }

}
