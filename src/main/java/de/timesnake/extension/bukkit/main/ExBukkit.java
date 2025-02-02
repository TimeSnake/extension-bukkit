/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.extension.bukkit.cmd.*;
import de.timesnake.library.chat.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ExBukkit extends JavaPlugin {

  public static org.bukkit.plugin.Plugin getPlugin() {
    return plugin;
  }

  private static ExBukkit plugin;

  @Override
  public void onEnable() {

    ExBukkit.plugin = this;

    PluginManager pm = Bukkit.getPluginManager();

    pm.registerEvents(new PreProcess(), plugin);
    pm.registerEvents(new CmdInventory(), plugin);

    //gamemode
    Server.getCommandManager()
        .addCommand(this, "gm", List.of("gamemode", "gmode"), new CmdGamemode(),
            Plugin.SERVER);

    //teleport
    Server.getCommandManager()
        .addCommand(this, "tp", List.of("teleport"), new CmdTeleport(), Plugin.SERVER);
    //tphere
    Server.getCommandManager()
        .addCommand(this, "teleporthere", List.of("tphere", "tph"), new CmdTeleport(),
            Plugin.SERVER);
    //tpa
    Server.getCommandManager()
        .addCommand(this, "teleportask", List.of("call", "tpask", "teleporta", "tpa"),
            new CmdTeleport(), Plugin.SERVER);
    //tphereask
    Server.getCommandManager()
        .addCommand(this, "teleportaskhere", List.of("tpahere", "tpaskhere", "tpaskh",
            "tpah"), new CmdTeleport(), Plugin.SERVER);
    //back
    Server.getCommandManager().addCommand(this, "tpback", new CmdTeleport(), Plugin.SERVER);

    //tpa settings
    Server.getCommandManager().addCommand(this, "tpaccept", new CmdTeleport(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "tpdeny", new CmdTeleport(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "tpatoggle", new CmdTeleport(), Plugin.SERVER);

    //spawn
    Server.getCommandManager().addCommand(this, "spawn", new CmdTeleport(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "setspawn", new CmdTeleport(), Plugin.SERVER);

    // tphall
    Server.getCommandManager()
        .addCommand(this, "tphall", List.of("tphereall"), new CmdTeleport(), Plugin.SERVER);

    //speed
    Server.getCommandManager().addCommand(this, "speed", new CmdSpeed(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "speedfly", new CmdSpeed(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "speedwalk", new CmdSpeed(), Plugin.SERVER);

    //time
    Server.getCommandManager().addCommand(this, "time", new CmdTime(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "day", new CmdTime(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "night", new CmdTime(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "noon", new CmdTime(), Plugin.SERVER);

    //weather
    Server.getCommandManager().addCommand(this, "weather", new CmdWeather(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "sun", new CmdWeather(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "rain", new CmdWeather(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "thunder", new CmdWeather(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "storm", new CmdWeather(), Plugin.SERVER);

    //inventory
    Server.getCommandManager().addCommand(this, "invclear", new CmdInventory(), Plugin.SERVER);
    Server.getCommandManager()
        .addCommand(this, "inventory", List.of("inv", "invsee"), new CmdInventory(),
            Plugin.SERVER);

    //fly
    Server.getCommandManager().addCommand(this, "fly", new CmdFly(), Plugin.SERVER);

    //vanish
    Server.getCommandManager()
        .addCommand(this, "vanish", List.of("v"), new CmdVanish(), Plugin.SERVER);

    //god
    Server.getCommandManager().addCommand(this, "god", new CmdGod(), Plugin.SERVER);

    //afk
    Server.getCommandManager()
        .addCommand(this, "afk", List.of("awayfromkeyboard"), new CmdAfk(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "afktoggle", new CmdAfk(), Plugin.SERVER);

    //heal
    Server.getCommandManager().addCommand(this, "heal", new HealCmd(), Plugin.SERVER);

    //kill
    Server.getCommandManager().addCommand(this, "kill", new CmdKill(), Plugin.SERVER);
    Server.getCommandManager().addCommand(this, "killall", new CmdKill(), Plugin.SERVER);

    Server.getCommandManager()
        .addCommand(this, "mw", List.of("multiw", "multiworld", "multiworlds"),
            new CmdWorld(), Plugin.SERVER);

    Server.getCommandManager()
        .addCommand(this, "chatcopy", List.of("cc", "ccopy"), new CmdChatCopy(),
            Plugin.SERVER);

    Server.getCommandManager()
        .addCommand(this, "nightvision", List.of("nivi"), new CmdNightVision(),
            Plugin.SERVER);
  }

}
