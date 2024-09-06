package dev.padrewin.coldmentions.listeners;

import dev.padrewin.coldmentions.ColdMentions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ColdMentions plugin;

    public ChatListener(ColdMentions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player sender = event.getPlayer();

        String mentionColor = plugin.getConfig().getString("mentions.color");

        if (message.contains("@everyone") && (sender.hasPermission("ColdMentions.Admin") || sender.hasPermission("ColdMentions.Everyone"))) {
            message = message.replace("@everyone", plugin.applyHexColors(mentionColor + "@everyone"));

            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.equals(sender)) {
                    plugin.getCommands().handleMention(onlinePlayer, sender, message);

                }
            }
        } else {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.equals(sender) && message.contains("@" + onlinePlayer.getName())) {
                    String coloredMention = plugin.applyHexColors(mentionColor + "@" + onlinePlayer.getName());
                    message = message.replace("@" + onlinePlayer.getName(), coloredMention);

                    plugin.getCommands().handleMention(onlinePlayer, sender, message);
                }
            }
        }

        for (String word : message.split(" ")) {
            if (word.startsWith("@")) {
                String mentionName = word.substring(1);
                if (plugin.getServer().getPlayer(mentionName) == null) {
                    String coloredMention = plugin.applyHexColors(mentionColor + word);
                    message = message.replace(word, coloredMention);
                }
            }
        }
        event.setMessage(message);
    }
}
