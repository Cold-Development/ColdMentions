package dev.padrewin.coldmentions.utils;

import dev.padrewin.coldmentions.ColdMentions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI; // Asigură-te că este importat corect

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionBar {

    private final ColdMentions plugin;

    public ActionBar(ColdMentions plugin) {
        this.plugin = plugin;
    }

    public void sendActionBarMessage(Player mentioned, Player mentioner, String type) {
        String actionBarMessage;

        if (type.equalsIgnoreCase("admin")) {
            actionBarMessage = plugin.getConfig().getString("actionbar.message_admin");
        } else if (type.equalsIgnoreCase("everyone")) {
            actionBarMessage = plugin.getConfig().getString("actionbar.message_everyone");
        } else {
            actionBarMessage = plugin.getConfig().getString("actionbar.message_player");
        }

        // Înlocuim placeholder-ele cu datele reale
        actionBarMessage = actionBarMessage.replace("%player%", mentioner.getName());

        // Verificăm dacă PlaceholderAPI este disponibil și procesăm mesajul
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            actionBarMessage = PlaceholderAPI.setPlaceholders(mentioner, actionBarMessage); // Setăm toate placeholder-ele, inclusiv prefixurile
        }

        // Apelăm metoda de transformare a codurilor hex pentru culori
        actionBarMessage = translateHexColorCodes(actionBarMessage);

        // Trimitem mesajul în ActionBar
        mentioned.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
    }

    private String translateHexColorCodes(String message) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = ChatColor.of("#" + hexColor).toString();
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        message = buffer.toString();

        // Convertim și orice alte coduri de culori care folosesc `&`
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
