/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
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
      EntityType.PAINTING,
      EntityType.ACACIA_BOAT, EntityType.BIRCH_BOAT, EntityType.DARK_OAK_BOAT, EntityType.JUNGLE_BOAT,
      EntityType.SPRUCE_BOAT, EntityType.OAK_BOAT, EntityType.MANGROVE_BOAT, EntityType.PALE_OAK_BOAT,
      EntityType.ACACIA_CHEST_BOAT, EntityType.BIRCH_CHEST_BOAT, EntityType.DARK_OAK_CHEST_BOAT,
      EntityType.JUNGLE_CHEST_BOAT, EntityType.SPRUCE_CHEST_BOAT, EntityType.OAK_CHEST_BOAT,
      EntityType.MANGROVE_CHEST_BOAT, EntityType.PALE_OAK_CHEST_BOAT,
      EntityType.MINECART,
      EntityType.FURNACE_MINECART,
      EntityType.CHEST_MINECART, EntityType.COMMAND_BLOCK_MINECART, EntityType.HOPPER_MINECART,
      EntityType.SPAWNER_MINECART,
      EntityType.TNT_MINECART);

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.kill");
  private final Code playerPerm = Plugin.SERVER.createPermssionCode("exbukkit.kill.player");
  private final Code typePerm = Plugin.SERVER.createPermssionCode("exbukkit.kill.type");
  private final Code allPerm = Plugin.SERVER.createPermssionCode("exbukkit.kill.all");

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
        sender.sendTDMessageCommandHelp("Kill all of a type (drops, mobs, ...)", "killall <type>");
        sender.sendTDMessageCommandHelp("Kill all types", "killall all");
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(Completion.ofPlayerNames().permission(this.playerPerm))
        .addArgument(new Completion(this.allPerm, "all"))
        .addArgument(new Completion(this.typePerm, "drops", "mobs", "monsters", "animals", "xps", "<mobType>").allowAny());
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void killPlayer(Sender sender, Arguments<Argument> args) {
    args.isLengthEqualsElseExit(1, true);

    sender.hasPermissionElseExit(this.playerPerm);
    args.get(0).assertElseExit(a -> a.isPlayerName(true));

    User user = args.get(0).toUser();
    sender.hasGroupRankLowerElseExit(user.getPermGroup().getRank(), true);

    user.kill();
    sender.sendPluginTDMessage("§sKilled §v" + user.getTDChatName());
  }

  public void killType(Sender sender, Argument arg) {
    if (!sender.hasPermission(this.typePerm)) {
      return;
    }

    World world = sender.getUser().getPlayer().getWorld();
    int numberEntities = 0;

    switch (arg.toLowerCase()) {
      case "drops", "drop" -> {
        for (Entity entity : world.getEntities()) {
          if (entity.getType().equals(EntityType.ITEM)) {
            entity.remove();
            numberEntities++;
          }
        }
        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s drops from world §v" + world.getName());
      }
      case "mobs" -> {
        for (Entity entity : world.getLivingEntities()) {
          if (!entity.getType().equals(EntityType.PLAYER)) {
            entity.remove();
            numberEntities++;
          }
        }
        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s mobs from world §v" + world.getName());
      }
      case "monsters", "monster" -> {
        for (Entity entity : world.getLivingEntities()) {
          if (this.monsters.contains(entity.getType())) {
            entity.remove();
            numberEntities++;
          }
        }
        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s monsters from world §v" + world.getName());
      }
      case "xps", "xp" -> {
        for (Entity entity : world.getEntities()) {
          if (entity.getType().equals(EntityType.EXPERIENCE_ORB) || entity.getType()
              .equals(EntityType.EXPERIENCE_ORB)) {
            entity.remove();
            numberEntities++;
          }
        }
        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s xps from world §v" + world.getName());
      }
      case "animals", "animal" -> {
        for (Entity entity : world.getLivingEntities()) {
          if (!entity.getType().equals(EntityType.PLAYER) && !this.monsters.contains(
              entity.getType())) {
            entity.remove();
            numberEntities++;
          }
        }
        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s animals from world §v" + world.getName());
      }
      case "all" -> this.killTypeAll(sender);
      default -> {
        EntityType type;
        try {
          type = EntityType.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException e) {
          sender.sendTDMessageCommandHelp("Kill all of a type (drops, mobs, ...)", "killall <type>");
          sender.sendTDMessageCommandHelp("Kill all types", "killall all");
          sender.sendMessageKillAllTypeNotExist(arg.getString());
          return;
        }

        for (Entity entity : world.getEntities()) {
          if (entity.getType().equals(type)) {
            entity.remove();
            numberEntities++;
          }
        }

        sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + " " + arg.toLowerCase() + "§s from world §v" + world.getName());
      }
    }
  }

  public void killTypeAll(Sender sender) {
    if (!sender.hasPermission(this.allPerm)) {
      return;
    }

    World world = sender.getUser().getPlayer().getWorld();
    int numberEntities = 0;
    for (Entity entity : world.getEntities()) {
      if (!entity.getType().equals(EntityType.PLAYER) && !this.excluded.contains(
          entity.getType())) {
        entity.remove();
        numberEntities++;
      }
    }
    sender.sendPluginTDMessage("§sRemoved §v" + numberEntities + "§s entities from world §v" + world.getName());
  }

}
