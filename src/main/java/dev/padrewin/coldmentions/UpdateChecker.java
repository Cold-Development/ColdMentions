package dev.padrewin.coldmentions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.latestVersion = plugin.getDescription().getVersion();
        this.updateAvailable = false;
    }

    public void checkForUpdateAndLog() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CompletableFuture.runAsync(() -> {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
                        if (scanner.hasNext()) {
                            latestVersion = scanner.next().trim();
                            String currentVersion = plugin.getDescription().getVersion().trim();

                            updateAvailable = !currentVersion.equalsIgnoreCase(latestVersion);

                            String ANSI_RESET = "\u001B[0m";
                            String ANSI_PURPLE = "\u001B[38;5;141m";
                            String ANSI_LIGHT_BLUE = "\u001B[1;34m";
                            String ANSI_LIGHT_RED = "\u001B[1;31m";
                            String ANSI_GREEN = "\u001B[32m";
                            String ANSI_RED = "\u001B[31m";

                            if (updateAvailable) {
                                Bukkit.getLogger().info("");
                                Bukkit.getLogger().info(ANSI_PURPLE + "[ColdMentions] " + ANSI_LIGHT_BLUE + "Checking for updates..." + ANSI_RESET);
                                Bukkit.getLogger().info(ANSI_PURPLE + "[ColdMentions] " + ANSI_RED + "Update available: " + ANSI_LIGHT_RED + latestVersion + " (You are running version " + currentVersion + ")" + ANSI_RESET);
                                Bukkit.getLogger().info(ANSI_PURPLE + "[ColdMentions] " + ANSI_GREEN + "You can download the latest version from here: " + getUpdateLink() + ANSI_RESET);
                                Bukkit.getLogger().info("");
                            } else {
                                Bukkit.getLogger().info("");
                                Bukkit.getLogger().info(ANSI_PURPLE + "[ColdMentions] " + ANSI_LIGHT_BLUE + "Checking for updates..." + ANSI_RESET);
                                Bukkit.getLogger().info(ANSI_PURPLE + "[ColdMentions] " + ANSI_GREEN + "No update available" + ANSI_RESET);
                                Bukkit.getLogger().info("");
                            }
                        }
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().severe("[winVanisher] Update check failed: " + e.getMessage());
                }
            });
        }, 20L * 30); // 60 seconds delay (20 ticks = 1 second)
    }

    public CompletableFuture<Boolean> checkForUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
                    if (scanner.hasNext()) {
                        latestVersion = scanner.next().trim();

                        String currentVersion = plugin.getDescription().getVersion().trim();

                        updateAvailable = !currentVersion.equalsIgnoreCase(latestVersion);
                    }
                }
            } catch (IOException e) {
                Bukkit.getLogger().severe("[ColdMentions] Update check failed: " + e.getMessage());
            }
            return updateAvailable;
        });
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getUpdateLink() {
        return "https://www.spigotmc.org/resources/" + resourceId + "/";
    }
}
