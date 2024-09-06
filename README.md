![](https://imgur.com/BNnCdVa.gif)<br>
![](https://imgur.com/JQAE5V1.png) - Color of the mention can be edited in `config.yml`

# 🧊 ColdMentions - Minecraft Plugin

**ColdMentions** is a Minecraft plugin designed to enhance player interactions by allowing players to mention each other with customizable messages, sounds, and visual notifications. The plugin allows players to mention other players in chat using `@name` or `@everyone`, triggering sound notifications, ActionBar messages, and titles on screen.

## 🚀 Features

1. **Mentions in Chat:**  
   - When a player is mentioned in chat (e.g., `@username`), a sound plays, and a message appears in their ActionBar and/or as a title on the screen.
   - Admins can bypass ignore settings and mention everyone using `@everyone`.

2. **ActionBar Notifications:**  
   - Mentions trigger a customizable message in the ActionBar.
   - Separate messages for regular players, admins, and mentions for `@everyone`.
   - Hex color support for all messages.
   
3. **Title Notifications:**  
   - Displays customizable titles and subtitles on the screen when a player is mentioned.
   - Supports Hex colors and placeholders using PlaceholderAPI.
   
4. **Sound Alerts:**  
   - Configurable sounds for when a player is mentioned.
   - Different sounds for regular player mentions and admin mentions.
   - Volume control for each sound category.

5. **Cooldown for Mentions:**  
   - Prevents spam by enforcing a cooldown between mentions.
   - Admins can bypass this cooldown with permission.

6. **Ignore Feature:**  
   - Players can ignore mentions from other players using the `/cm ignore <player>` command.
   - Admins can bypass ignore settings.
   - **Ignore information is saved into SQLite database**.
   
7. **Update Notifications:**  
   - Notifies admins about available plugin updates on login.

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/cm help` | Displays the help menu with available commands. | `None` |
| `/cm reload` | Reloads the plugin configuration. | `ColdMentions.Admin` |
| `/cm toggle <player>` | Toggles mentions for a player. | `ColdMentions.Admin` |
| `/cm ignore <player>` | Ignore mentions from a player. | `ColdMentions.Ignore` |

## 🛠️ Permissions

- `ColdMentions.Admin`: Grants full access to all commands and features, including bypassing cooldown and ignore settings.
- `ColdMentions.Everyone` or `ColdMentions.Admin`: Allows to mention `@everyone` in chat.
- `ColdMentions.Ignore`: Allows players to use the ignore command to block mentions from specific players. (`ColdMentions.Admin` can bypass this)

## ⚙️ Configuration

### `config.yml`
```yaml
# General settings
mentions:
  enabled: true # Enables or disables the general mention functionality
  color: '&#FF0000' # The color of the mention (default is red)

settings:
  prefix: "&8「&#00D6FFC&#00C3FFo&#00B0FFl&#009EFFd&#008BFFM&#0078FFe&#0065FFn&#0052FFt&#003FFFi&#002DFFo&#001AFFn&#0007FFs&8」&7» " # The prefix that appears before each message in chat

# Cooldown settings for mentions
cooldown:
  enabled: true # Enables or disables the mention cooldown
  time: 5 # Cooldown time in seconds

# Custom messages
messages:
  config_reloaded: '&7Config &areloaded&f!' # Message displayed when the config is reloaded
  mention_enabled_by_admin: '&cYour mentions have been enabled by an admin.' # Message sent when mentions are enabled by an admin
  mention_disabled_by_admin: '&cYour mentions have been disabled by an admin.' # Message sent when mentions are disabled by an admin
  mention_toggled_for_player: '&7Mentions status toggled for %player%.' # Message sent when mention status is toggled for a player
  player_not_found: '&cPlayer not found!' # Message displayed when the specified player is not found
  no_permission: '&cYou don''t have permission to do that.' # Message shown when a player doesn't have the necessary permissions
  invalid_usage: '&cInvalid command usage.' # Message shown when a command is used incorrectly
  ignore_added: '&7You are now ignoring mentions from %player%.' # Message shown when a player starts ignoring mentions from another player
  ignore_removed: '&7You are no longer ignoring mentions from %player%.' # Message shown when a player stops ignoring mentions from another player
  mention_cooldown: "&cYou must wait %time% seconds before mentioning again." # Message displayed when the player tries to mention someone before the cooldown period has expired
  update_notification: "&cA new version &4%latest_version%&c was found &4(your version %current_version%)&7 &cUpdate here: %update_link%"

# Sound settings for mentions
#   Check out this link if you want to change default sounds
#     https://github.com/Cold-Development/ColdMentions/wiki/Minecraft-sounds-for-ColdMentions
sounds:
  enabled: true # Enable or disable sounds when someone is mentioned
  mention_sound: ENTITY_PLAYER_LEVELUP # The sound played when someone is mentioned
  admin_mention_sound: ENTITY_ENDER_DRAGON_GROWL # The sound played when an admin mentions someone
  mention_volume: 1.0 # The volume of the mention sound (range: 0.0 - 1.0)
  admin_mention_volume: 1.0 # The volume of the admin mention sound (range: 0.0 - 1.0)

# Title displayed on the screen
titles:
  enabled: true # Enables or disables titles displayed on the screen
  fade_in: 10 # Duration of the fade-in effect for the title in ticks
  stay: 70 # Duration for which the title remains visible
  fade_out: 20 # Duration of the fade-out effect for the title

  # Settings for regular players
  player:
    title_text: '&#FF4500%luckperms_prefix%&4%player% &#FF4500mentioned you!' # The message displayed as a title for players when mentioned
    subtitle_text: '&#FF0000Check the chat for details!' # The message displayed as a subtitle for players when mentioned

  # Settings for administrators
  admin:
    title_text: '&#FF4500%luckperms_prefix%&4%player% &#FF4500mentioned you!' # The message displayed as a title for admins when mentioned
    subtitle_text: '&#FF0000C&#FF0202h&#FE0505e&#FE0707c&#FE0A0Ak &#FD0F0Ft&#FC1111h&#FC1414e
      &#FB1919c&#FB1B1Bh&#FB1E1Ea&#FA2020t &#F92525f&#F92828o&#F92A2Ar &#F82F2Fa&#F83131d&#F73434m&#F73636i&#F63939n
      &#F63E3Em&#F54040e&#F54343s&#F54545s&#F44848a&#F44A4Ag&#F34D4De&#F34F4F!' # A more stylized message displayed as a subtitle for admins when mentioned

  # Settings for @everyone mention
  everyone:
    title_text: '&#FF4500%luckperms_prefix%&4%player% &#FF4500mentioned everyone!' # The message displayed as a title when an admin uses @everyone to mention all players
    subtitle_text: '&#FF0000Check the chat for details!' # The message displayed as a subtitle when an admin uses @everyone to mention all players

# Action bar configuration
actionbar:
  enabled: true  # Enables or disables messages in ActionBar
  message_admin: "&#00FF00%luckperms_prefix%&7%player%! mentioned you"
  message_player: "&#00FF00%luckperms_prefix%&7%player% mentioned you!"
  message_everyone: "&#00FF00%luckperms_prefix%&7%player% mentioned @everyone!"
```

## 🌐 Sound Configuration

To explore all available Minecraft sounds for this plugin, visit the [ColdMentions Wiki Sound Page](https://github.com/Cold-Development/ColdMentions/wiki/Minecraft-sounds-for-ColdMentions).

## 🔄 Update Checker

ColdMentions includes an automatic update checker for admins. When an admin logs in, the plugin checks for updates and notifies them if a new version is available.

## 📦 Installation

1. Download the latest version of ColdMentions.
2. Place the plugin JAR file in your server's `plugins` folder.
3. Start your server to generate the `config.yml` file.
4. Customize the configuration to your liking and reload the plugin with `/cm reload`.

#### Downloads:
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/Cold-Development/ColdMentions/total?color=green)
