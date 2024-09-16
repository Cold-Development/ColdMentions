package dev.padrewin.coldmentions;

import dev.padrewin.coldmentions.commands.Commands;
import dev.padrewin.coldmentions.database.DatabaseManager;
import dev.padrewin.coldmentions.listeners.ChatListener;
import dev.padrewin.coldmentions.utils.ActionBar;
import dev.padrewin.coldplugin.ColdPlugin;
import dev.padrewin.coldplugin.manager.Manager;
import dev.padrewin.coldplugin.manager.PluginUpdateManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColdMentions extends ColdPlugin {

    private ColdMentions instance;
    private Commands commands;
    private DatabaseManager databaseManager;
    private ActionBar actionBar;

    String ANSI_RESET = "\u001B[0m";
    String ANSI_AQUA = "\u001B[36m";
    String ANSI_PURPLE = "\u001B[35m";

    public ColdMentions() {
        super("Cold-Development", "ColdMentions", 23386, null, null, null);
        instance = this;
    }

    @Override
    public void enable() {
        getManager(PluginUpdateManager.class);
        instance = this;
        try {
            databaseManager = new DatabaseManager(this, "coldmentions.db");
            getLogger().info("\u001B[32mDatabase path: " + new File(getDataFolder(), "coldmentions.db").getAbsolutePath() + "\u001B[0m");
        } catch (SQLException e) {
            getLogger().severe("Failed to initialize the database!");
            e.printStackTrace();
        }

        String name = getDescription().getName();
        getLogger().info("");
        getLogger().info(ANSI_AQUA + "  ____ ___  _     ____  " + ANSI_RESET);
        getLogger().info(ANSI_AQUA + " / ___/ _ \\| |   |  _ \\ " + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "| |  | | | | |   | | | |" + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "| |__| |_| | |___| |_| |" + ANSI_RESET);
        getLogger().info(ANSI_AQUA + " \\____\\___/|_____|____/ " + ANSI_RESET);
        getLogger().info("    " + ANSI_AQUA + name + " v" + getDescription().getVersion() + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + "    Author(s): " + ANSI_PURPLE + getDescription().getAuthors().get(0) + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "    (c) Cold Development. All rights reserved." + ANSI_RESET);
        getLogger().info("");

        saveDefaultConfig();

        this.commands = new Commands(this);
        getCommand("cm").setExecutor(this.commands);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.actionBar = new ActionBar(this);
    }

    @Override
    public void disable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().info("");
        getLogger().info("ColdMentions disabled.");
        getLogger().info("");
    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Commands getCommands() {
        return commands;
    }

    public String applyHexColors(String message) {
        if (message == null) return null;

        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = "§x";
            for (char c : hexColor.toCharArray()) {
                replacement += "§" + c;
            }
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public String getPrefixedMessage(String path) {
        String prefix = applyHexColors(getConfig().getString("settings.prefix"));
        String message = applyHexColors(getConfig().getString(path));
        return prefix + " " + message;
    }

}
