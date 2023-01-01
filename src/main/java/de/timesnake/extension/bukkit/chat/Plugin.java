/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.bukkit.chat;

import de.timesnake.library.basic.util.LogHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends de.timesnake.basic.bukkit.util.chat.Plugin {

    public static final Plugin BUKKIT = new Plugin("Bukkit", "EXB", LogHelper.getLogger("Bukkit", Level.INFO));

    protected Plugin(String name, String code, Logger logger) {
        super(name, code, logger);
    }
}
