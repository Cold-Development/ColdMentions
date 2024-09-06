package dev.padrewin.coldmentions.commands;

import dev.padrewin.coldmentions.ColdMentions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Commands implements CommandExecutor {

    private final ColdMentions plugin;
    private final HashMap<UUID, Long> mentionCooldowns = new HashMap<>();

    public Commands(ColdMentions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cm")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("ColdMentions.Admin")) {
                    reloadConfig(sender);
                } else {
                    String noPermissionMessage = plugin.getPrefixedMessage("messages.no_permission");
                    sender.sendMessage(plugin.applyHexColors(noPermissionMessage));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("toggle")) {
                if (args.length == 2 && sender.hasPermission("ColdMentions.Admin")) {
                    toggleMentionForPlayer(sender, args[1]);
                } else {
                    String invalidUsageMessage = plugin.getPrefixedMessage("messages.invalid_usage");
                    sender.sendMessage(plugin.applyHexColors(invalidUsageMessage));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("ignore")) {
                if (args.length == 2 && sender instanceof Player) {
                    Player player = (Player) sender;

                    if (!player.hasPermission("coldmentions.ignore")) {
                        String noPermissionMessage = plugin.getPrefixedMessage("messages.no_permission");
                        player.sendMessage(plugin.applyHexColors(noPermissionMessage));
                        return true;
                    }

                    Player target = plugin.getServer().getPlayer(args[1]);

                    if (target == null) {
                        String playerNotFoundMessage = plugin.getPrefixedMessage("messages.player_not_found");
                        sender.sendMessage(plugin.applyHexColors(playerNotFoundMessage));
                        return true;
                    }

                    ignorePlayer(player, target);
                } else {
                    String invalidUsageMessage = plugin.getPrefixedMessage("messages.invalid_usage");
                    sender.sendMessage(plugin.applyHexColors(invalidUsageMessage));
                }
                return true;
            }
        }
        return false;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(plugin.applyHexColors("&8「&#00D6FFC&#00C3FFo&#00B0FFl&#009EFFd&#008BFFM&#0078FFe&#0065FFn&#0052FFt&#003FFFi&#002DFFo&#001AFFn&#0007FFs&8」 &c- v" + plugin.getDescription().getVersion()));
        sender.sendMessage(plugin.applyHexColors("&d/cm help &#FFFFFF- Show available commands."));
        sender.sendMessage(plugin.applyHexColors("&d/cm reload &#FFFFFF- Reload plugin configuration."));
        sender.sendMessage(plugin.applyHexColors("&d/cm toggle &7<player> &#FFFFFF- Toggle mentions for a player (Admin only)."));
        sender.sendMessage(plugin.applyHexColors("&d/cm ignore &7<player> &#FFFFFF- Ignore mentions from a player."));
    }

    private void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        String reloadMessage = plugin.getPrefixedMessage("messages.config_reloaded");
        sender.sendMessage(plugin.applyHexColors(reloadMessage));
    }

    private void toggleMentionForPlayer(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) {
            boolean status = !plugin.getConfig().getBoolean("mentions." + target.getUniqueId(), true);
            plugin.getConfig().set("mentions." + target.getUniqueId(), status);
            plugin.saveConfig();

            String messageForTarget = status
                    ? plugin.getPrefixedMessage("messages.mention_enabled_by_admin")
                    : plugin.getPrefixedMessage("messages.mention_disabled_by_admin");
            target.sendMessage(plugin.applyHexColors(messageForTarget));

            String messageForSender = plugin.getPrefixedMessage("messages.mention_toggled_for_player")
                    .replace("%player%", playerName);
            sender.sendMessage(plugin.applyHexColors(messageForSender));
        } else {
            String playerNotFoundMessage = plugin.getPrefixedMessage("messages.player_not_found");
            sender.sendMessage(plugin.applyHexColors(playerNotFoundMessage));
        }
    }

    public void ignorePlayer(Player player, Player target) {
        if (plugin.getDatabaseManager().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            plugin.getDatabaseManager().removeIgnore(player.getUniqueId(), target.getUniqueId());
            String ignoreRemovedMessage = plugin.getPrefixedMessage("messages.ignore_removed").replace("%player%", target.getName());
            player.sendMessage(plugin.applyHexColors(ignoreRemovedMessage));
        } else {
            plugin.getDatabaseManager().addIgnore(player.getUniqueId(), player.getName(), target.getUniqueId(), target.getName());
            String ignoreAddedMessage = plugin.getPrefixedMessage("messages.ignore_added").replace("%player%", target.getName());
            player.sendMessage(plugin.applyHexColors(ignoreAddedMessage));
        }
    }

    private boolean isInCooldown(Player player) {
        if (player.hasPermission("ColdMentions.Admin")) {
            return false;
        }

        long cooldownTime = plugin.getConfig().getInt("cooldown.time") * 1000L;
        long currentTime = System.currentTimeMillis();

        if (mentionCooldowns.containsKey(player.getUniqueId())) {
            long lastMentionTime = mentionCooldowns.get(player.getUniqueId());
            if ((currentTime - lastMentionTime) < cooldownTime) {
                long timeLeft = cooldownTime - (currentTime - lastMentionTime);
                String cooldownMessage = plugin.getPrefixedMessage("messages.mention_cooldown")
                        .replace("%time%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeLeft)));
                player.sendMessage(plugin.applyHexColors(cooldownMessage));
                return true;
            }
        }

        mentionCooldowns.put(player.getUniqueId(), currentTime);
        return false;
    }

    public void handleMention(Player mentioned, Player mentioner, String message) {
        if (!plugin.getConfig().getBoolean("mentions.enabled")) {
            return;
        }

        if (plugin.getConfig().getBoolean("cooldown.enabled") && isInCooldown(mentioner)) {
            return;
        }

        if (!mentioner.hasPermission("ColdMentions.Admin") && plugin.getDatabaseManager().isIgnored(mentioned.getUniqueId(), mentioner.getUniqueId())) {
            return;
        }

        String titleAdmin = plugin.getConfig().getString("titles.admin.title_text");
        String titlePlayer = plugin.getConfig().getString("titles.player.title_text");
        String titleEveryone = plugin.getConfig().getString("titles.everyone.title_text");

        String subtitleAdmin = plugin.getConfig().getString("titles.admin.subtitle_text");
        String subtitlePlayer = plugin.getConfig().getString("titles.player.subtitle_text");
        String subtitleEveryone = plugin.getConfig().getString("titles.everyone.subtitle_text");

        String prefixAdmin = PlaceholderAPI.setPlaceholders(mentioner, "%luckperms_prefix%").trim();
        String prefixPlayer = PlaceholderAPI.setPlaceholders(mentioner, "%luckperms_prefix%").trim();

        titleAdmin = PlaceholderAPI.setPlaceholders(mentioner, titleAdmin.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixAdmin));
        titlePlayer = PlaceholderAPI.setPlaceholders(mentioner, titlePlayer.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixPlayer));
        titleEveryone = PlaceholderAPI.setPlaceholders(mentioner, titleEveryone.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixAdmin));

        subtitleAdmin = PlaceholderAPI.setPlaceholders(mentioner, subtitleAdmin.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixAdmin));
        subtitlePlayer = PlaceholderAPI.setPlaceholders(mentioner, subtitlePlayer.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixPlayer));
        subtitleEveryone = PlaceholderAPI.setPlaceholders(mentioner, subtitleEveryone.replace("%player%", mentioner.getName()).replace("%luckperms_prefix%", prefixAdmin));

        if (message.contains("@everyone") && (mentioner.hasPermission("ColdMentions.Everyone") || mentioner.hasPermission("ColdMentions.Admin"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(mentioner)) {
                    if (plugin.getConfig().getBoolean("sounds.enabled")) {
                        Sound sound = mentioner.hasPermission("ColdMentions.Admin")
                                ? Sound.valueOf(plugin.getConfig().getString("sounds.admin_mention_sound"))
                                : Sound.valueOf(plugin.getConfig().getString("sounds.mention_sound"));
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    }

                    if (plugin.getConfig().getBoolean("titles.enabled")) {
                        String titleText = plugin.applyHexColors(titleEveryone);
                        String subtitleText = plugin.applyHexColors(subtitleEveryone);
                        player.sendTitle(titleText, subtitleText, plugin.getConfig().getInt("titles.fade_in"), plugin.getConfig().getInt("titles.stay"), plugin.getConfig().getInt("titles.fade_out"));
                    }

                    if (plugin.getConfig().getBoolean("actionbar.enabled")) {
                        plugin.getActionBar().sendActionBarMessage(player, mentioner, "everyone");
                    }
                }
            }
            return;
        }

        List<UUID> ignoredPlayers = plugin.getDatabaseManager().getIgnoredPlayers(mentioned.getUniqueId());

        if (!mentioner.hasPermission("ColdMentions.Admin") && ignoredPlayers.contains(mentioner.getUniqueId())) {
            return;
        }

        if (plugin.getConfig().getBoolean("sounds.enabled")) {
            Sound sound = mentioner.hasPermission("ColdMentions.Admin")
                    ? Sound.valueOf(plugin.getConfig().getString("sounds.admin_mention_sound"))
                    : Sound.valueOf(plugin.getConfig().getString("sounds.mention_sound"));

            float volume = mentioner.hasPermission("ColdMentions.Admin")
                    ? (float) plugin.getConfig().getDouble("sounds.admin_mention_volume", 1.0)
                    : (float) plugin.getConfig().getDouble("sounds.mention_volume", 1.0);

            mentioned.playSound(mentioned.getLocation(), sound, volume, 1.0f);
        }

        if (plugin.getConfig().getBoolean("titles.enabled")) {
            String titleText = mentioner.hasPermission("ColdMentions.Admin")
                    ? plugin.applyHexColors(titleAdmin)
                    : plugin.applyHexColors(titlePlayer);

            String subtitleText = mentioner.hasPermission("ColdMentions.Admin")
                    ? plugin.applyHexColors(subtitleAdmin)
                    : plugin.applyHexColors(subtitlePlayer);

            mentioned.sendTitle(titleText, subtitleText, plugin.getConfig().getInt("titles.fade_in"), plugin.getConfig().getInt("titles.stay"), plugin.getConfig().getInt("titles.fade_out"));
        }

        if (plugin.getConfig().getBoolean("actionbar.enabled")) {
            String mentionType = mentioner.hasPermission("ColdMentions.Admin") ? "admin" : "player";
            plugin.getActionBar().sendActionBarMessage(mentioned, mentioner, mentionType);
        }
    }
}
