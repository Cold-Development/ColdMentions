package dev.padrewin.coldmentions.listeners;

import dev.padrewin.coldmentions.ColdMentions;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ColdMentions plugin;

    public JoinListener(ColdMentions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("coldmentions.admin")) {
            plugin.getUpdateChecker().checkForUpdate().thenAccept(updateAvailable -> {
                if (updateAvailable) {
                    String currentVersion = plugin.getDescription().getVersion().trim();
                    String latestVersion = plugin.getUpdateChecker().getLatestVersion().trim();
                    String updateLink = plugin.getUpdateChecker().getUpdateLink();

                    String updateMessage = plugin.getConfig().getString("messages.update_notification");
                    if (updateMessage != null && !updateMessage.isEmpty()) {
                        updateMessage = updateMessage
                                .replace("%latest_version%", latestVersion)
                                .replace("%current_version%", currentVersion)
                                .replace("%update_link%", updateLink);

                        String prefix = plugin.getConfig().getString("settings.prefix", "");
                        String finalUpdateMessage = plugin.applyHexColors(prefix + updateMessage);

                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            event.getPlayer().sendMessage(finalUpdateMessage);
                        }, 50L);
                    }
                }
            });
        }
    }
}
