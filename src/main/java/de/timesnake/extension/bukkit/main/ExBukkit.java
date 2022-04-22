package de.timesnake.extension.bukkit.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.extension.bukkit.chat.Plugin;
import de.timesnake.extension.bukkit.cmd.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ExBukkit extends JavaPlugin {

    private static ExBukkit plugin;

    @Override
    public void onEnable() {

        ExBukkit.plugin = this;

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PreProcess(), plugin);
        pm.registerEvents(new CmdInventory(), plugin);

        //gamemode
        Server.getCommandManager().addCommand(this, "gm", List.of("gamemode", "gmode"), new CmdGamemode(), Plugin.BUKKIT);

        //teleport
        Server.getCommandManager().addCommand(this, "tp", List.of("teleport"), new CmdTeleport(), Plugin.BUKKIT);
        //tphere
        Server.getCommandManager().addCommand(this, "teleporthere", List.of("tphere", "tph"), new CmdTeleport(), Plugin.BUKKIT);
        //tpa
        Server.getCommandManager().addCommand(this, "teleportask", List.of("call", "tpask", "teleporta", "tpa"), new CmdTeleport(), Plugin.BUKKIT);
        //tphereask
        Server.getCommandManager().addCommand(this, "teleportaskhere", List.of("tpahere", "tpaskhere", "tpaskh", "tpah"), new CmdTeleport(), Plugin.BUKKIT);
        //back
        Server.getCommandManager().addCommand(this, "back", new CmdTeleport(), Plugin.BUKKIT);

        //tpa settings
        Server.getCommandManager().addCommand(this, "tpaccept", new CmdTeleport(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "tpdeny", new CmdTeleport(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "tpatoggle", new CmdTeleport(), Plugin.BUKKIT);

        //spawn
        Server.getCommandManager().addCommand(this, "spawn", new CmdTeleport(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "setspawn", new CmdTeleport(), Plugin.BUKKIT);

        // tphall
        Server.getCommandManager().addCommand(this, "tphall", List.of("tphereall"), new CmdTeleport(), Plugin.BUKKIT);

        //speed
        Server.getCommandManager().addCommand(this, "speed", new CmdSpeed(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "speedfly", new CmdSpeed(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "speedwalk", new CmdSpeed(), Plugin.BUKKIT);

        //time
        Server.getCommandManager().addCommand(this, "time", new CmdTime(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "day", new CmdTime(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "night", new CmdTime(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "noon", new CmdTime(), Plugin.BUKKIT);

        //weather
        Server.getCommandManager().addCommand(this, "weather", new CmdWeather(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "sun", new CmdWeather(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "rain", new CmdWeather(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "thunder", new CmdWeather(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "storm", new CmdWeather(), Plugin.BUKKIT);

        //inventory
        Server.getCommandManager().addCommand(this, "invclear", new CmdInventory(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "inventory", List.of("inv", "invsee"), new CmdInventory(), Plugin.BUKKIT);

        //fly
        Server.getCommandManager().addCommand(this, "fly", new CmdFly(), Plugin.BUKKIT);

        //vanish
        Server.getCommandManager().addCommand(this, "vanish", List.of("v"), new CmdVanish(), Plugin.BUKKIT);

        //god
        Server.getCommandManager().addCommand(this, "god", new CmdGod(), Plugin.BUKKIT);

        //afk
        Server.getCommandManager().addCommand(this, "afk", List.of("awayfromkeyboard"), new CmdAfk(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "afktoggle", new CmdAfk(), Plugin.BUKKIT);

        //heal
        Server.getCommandManager().addCommand(this, "heal", new HealCmd(), Plugin.BUKKIT);

        //kill
        Server.getCommandManager().addCommand(this, "kill", new CmdKill(), Plugin.BUKKIT);
        Server.getCommandManager().addCommand(this, "killall", new CmdKill(), Plugin.BUKKIT);

        Server.getCommandManager().addCommand(this, "mw", List.of("multiw", "multiworld", "multiworlds"), new CmdWorld(), Plugin.BUKKIT);

        Server.getCommandManager().addCommand(this, "chatcopy", List.of("cc", "ccopy"), new CmdChatCopy(), Plugin.BUKKIT);
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }

}
