/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.cmd;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Set;

public class CmdAfk implements CommandListener {


  public Set<User> users = new HashSet<>();
  public Set<User> toggledUsers = new HashSet<>();

  private final Code perm = Plugin.SERVER.createPermssionCode("exbukkit.afk");
  private final Code togglePerm = Plugin.SERVER.createPermssionCode("exbukkit.afk.toggle");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd,
      Arguments<Argument> args) {

    if (cmd.getName().equalsIgnoreCase("afk") || cmd.getName()
        .equalsIgnoreCase("awayfromkeyboard")) {
      if (!sender.hasPermission(this.perm)) {
        return;
      }

      if (!sender.isPlayer(true)) {
        return;
      }

      User user = sender.getUser();

      if (!this.users.contains(user)) {
        this.afk(user);
      } else {
        this.unAfk(user);
      }
    } else if (cmd.getName().equalsIgnoreCase("afktoggle")) {
      if (!sender.hasPermission(this.togglePerm)) {
        return;
      }

      if (!sender.isPlayer(true)) {
        return;
      }

      User user = sender.getUser();
      if (!this.toggledUsers.contains(user)) {
        this.toggleAfk(user);
      } else {
        this.unToggleAfk(user);
      }
    }

  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm);
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }

  public void afk(User user) {
    if (this.users.contains(user)) {
      return;
    }

    this.users.add(user);
    Server.broadcastMessage(Plugin.SERVER, user.getChatNameComponent()
        .append(Component.text(" is now afk", ExTextColor.PUBLIC)));

  }

  public void unAfk(User user) {
    if (!this.users.contains(user)) {
      return;
    }

    this.users.remove(user);
    Server.broadcastMessage(Plugin.SERVER, user.getChatNameComponent()
        .append(Component.text(" is no longer afk", ExTextColor.PUBLIC)));

  }

  public void toggleAfk(User user) {
    if (this.toggledUsers.contains(user)) {
      return;
    }

    this.toggledUsers.add(user);
    user.sendPluginMessage(Plugin.SERVER,
        Component.text(" disabled auto-afk", ExTextColor.PERSONAL));
  }

  public void unToggleAfk(User user) {
    if (!this.toggledUsers.contains(user)) {
      return;
    }

    this.toggledUsers.remove(user);
    user.sendPluginMessage(Plugin.SERVER,
        Component.text(" enabled auto-afk", ExTextColor.PERSONAL));
  }
}
