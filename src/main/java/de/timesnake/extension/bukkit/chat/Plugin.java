package de.timesnake.extension.bukkit.chat;

public class Plugin extends de.timesnake.basic.bukkit.util.chat.Plugin {

    public static final Plugin BUKKIT = new Plugin("Bukkit", "EXB");

    protected Plugin(String name, String code) {
        super(name, code);
    }
}
