/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CmdKill implements CommandListener {

  private final List<EntityType> monsters = List.of(EntityType.BLAZE, EntityType.CAVE_SPIDER,
      EntityType.CREEPER,
      EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.GHAST, EntityType.GIANT,
      EntityType.ENDER_DRAGON,
      EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.MAGMA_CUBE, EntityType.PHANTOM,
      EntityType.ZOMBIFIED_PIGLIN,
      EntityType.PIGLIN, EntityType.PILLAGER, EntityType.SHULKER, EntityType.SILVERFISH,
      EntityType.SKELETON,
      EntityType.SLIME, EntityType.SPIDER, EntityType.VEX, EntityType.WITCH,
      EntityType.WITHER,
      EntityType.WITHER_SKELETON,
      EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.VINDICATOR, EntityType.EVOKER,
      EntityType.EVOKER_FANGS);

  private final List<EntityType> excluded = List.of(EntityType.ARMOR_STAND, EntityType.ITEM_FRAME,
      EntityType.GLOW_ITEM_FRAME,
      EntityType.PAINTING, EntityType.BOAT, EntityType.BOAT, EntityType.MINECART,
      EntityType.MINECART_FURNACE,
      EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND, EntityType.MINECART_HOPPER,
      EntityType.MINECART_MOB_SPAWNER,
      EntityType.MINECART_TNT);

  private final Code perm = Plugin.BUKKIT.createPermssionCode("exbukkit.kill");
  private final Code playerPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.kill.player");
  private final Code typePerm = Plugin.BUKKIT.createPermssionCode("exbukkit.kill.type");
  private final Code allPerm = Plugin.BUKKIT.createPermssionCode("exbukkit.kill.all");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
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
        sender.sendTDMessageCommandHelp("Kill all of a type (drops, mobs, ...)",
            "killall <type>");
        sender.sendTDMessageCommandHelp("Kill all types", "killall all");
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(Completion.ofPlayerNames().permission(this.playerPerm))
        .addArgument(new Completion(this.allPerm, "all"))
        .addArgument(new Completion(this.typePerm, "drops", "mobs", "monsters", "animals", "xps", "<mobType>"));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void killPlayer(Sender sender, Arguments<Argument> args) {
    if (!args.isLengthEquals(1, true)) {
      sender.sendTDMessageCommandHelp("Kill a player", "kill <player>");
      return;
    }

    if (!sender.hasPermission(this.playerPerm)) {
      return;
    }

    if (!args.get(0).isPlayerName(true)) {
      return;
    }

    User user = args.get(0).toUser();
    if (!sender.hasGroupRankLowerEquals(user.getPermGroup().getRank())) {
      return;
    }

    user.getPlayer().setHealth(0);
  }

  public void killType(Sender sender, Argument arg) {
    if (!sender.hasPermission(this.typePerm)) {
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
        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" drops from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
      case "mobs" -> {
        for (Entity entity : w.getLivingEntities()) {
          if (!entity.getType().equals(EntityType.PLAYER)) {
            entity.remove();
            i++;
          }
        }
        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" mobs from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
      case "monsters", "monster" -> {
        for (Entity entity : w.getLivingEntities()) {
          if (this.monsters.contains(entity.getType())) {
            entity.remove();
            i++;
          }
        }
        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" monsters from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
      case "xps", "xp" -> {
        for (Entity entity : w.getEntities()) {
          if (entity.getType().equals(EntityType.THROWN_EXP_BOTTLE) || entity.getType()
              .equals(EntityType.EXPERIENCE_ORB)) {
            entity.remove();
            i++;
          }
        }
        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" xps from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
      case "animals", "animal" -> {
        for (Entity entity : w.getLivingEntities()) {
          if (!entity.getType().equals(EntityType.PLAYER) && !this.monsters.contains(
              entity.getType())) {
            entity.remove();
            i++;
          }
        }
        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" animals from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
      case "all" -> this.killTypeAll(sender);
      default -> {
        EntityType type;
        try {
          type = EntityType.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException e) {
          sender.sendTDMessageCommandHelp("Kill all of a type (drops, mobs, ...)",
              "killall <type>");
          sender.sendTDMessageCommandHelp("Kill all types", "killall all");
          sender.sendMessageKillAllTypeNotExist(arg.getString());
          return;
        }

        for (Entity entity : w.getEntities()) {
          if (entity.getType().equals(type)) {
            entity.remove();
            i++;
          }
        }

        sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
            .append(Component.text(i, ExTextColor.VALUE))
            .append(Component.text(" " + arg.toLowerCase(), ExTextColor.VALUE))
            .append(Component.text(" from world ", ExTextColor.PERSONAL))
            .append(Component.text(w.getName(), ExTextColor.VALUE)));
      }
    }
  }

  public void killTypeAll(Sender sender) {
    if (!sender.hasPermission(this.allPerm)) {
      return;
    }

    World w = sender.getUser().getPlayer().getWorld();
    int i = 0;
    for (Entity entity : w.getEntities()) {
      if (!entity.getType().equals(EntityType.PLAYER) && !this.excluded.contains(
          entity.getType())) {
        entity.remove();
        i++;
      }
    }
    sender.sendPluginMessage(Component.text("Removed ", ExTextColor.PERSONAL)
        .append(Component.text(i, ExTextColor.VALUE))
        .append(Component.text(" entities from world ", ExTextColor.PERSONAL))
        .append(Component.text(w.getName(), ExTextColor.VALUE)));
  }

}
