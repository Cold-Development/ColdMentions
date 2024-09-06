package dev.padrewin.coldmentions.database;

import dev.padrewin.coldmentions.ColdMentions;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final ColdMentions plugin;
    private final Connection dbConnection;
    private Connection connection;

    public DatabaseManager(ColdMentions plugin, String dbName) throws SQLException {
        this.plugin = plugin;
        connect();
        String dbPath = plugin.getDataFolder() + "/" + dbName;
        dbPath = "jdbc:sqlite:" + dbPath;
        this.dbConnection = DriverManager.getConnection(dbPath);

        createTables();
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS ignores ("
                + "player_uuid TEXT NOT NULL,"
                + "ignored_player_uuid TEXT NOT NULL,"
                + "PRIMARY KEY (player_uuid, ignored_player_uuid)"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create the 'ignores' table!");
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            String dbPath = dataFolder.getAbsolutePath() + File.separator + "coldmentions.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            plugin.getLogger().info("Database connected successfully.");

            createTable();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize the database!");
            e.printStackTrace();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ignores ("
                + "player_uuid TEXT NOT NULL,"
                + "player_name TEXT NOT NULL,"
                + "ignored_player_uuid TEXT NOT NULL,"
                + "ignored_player_name TEXT NOT NULL,"
                + "PRIMARY KEY (player_uuid, ignored_player_uuid)"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create the 'ignores' table!");
            e.printStackTrace();
        }
    }

    public boolean isIgnored(UUID player, UUID ignoredPlayer) {
        String query = "SELECT * FROM ignores WHERE player_uuid = ? AND ignored_player_uuid = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, ignoredPlayer.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addIgnore(UUID player, String playerName, UUID ignoredPlayer, String ignoredPlayerName) {
        String insert = "INSERT INTO ignores (player_uuid, player_name, ignored_player_uuid, ignored_player_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(insert)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, ignoredPlayer.toString());
            stmt.setString(4, ignoredPlayerName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeIgnore(UUID player, UUID ignoredPlayer) {
        String delete = "DELETE FROM ignores WHERE player_uuid = ? AND ignored_player_uuid = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(delete)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, ignoredPlayer.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UUID> getIgnoredPlayers(UUID playerUUID) {
        List<UUID> ignoredPlayers = new ArrayList<>();
        String sql = "SELECT ignored_player_uuid FROM ignores WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ignoredPlayers.add(UUID.fromString(rs.getString("ignored_player_uuid")));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get ignored players!");
            e.printStackTrace();
        }

        return ignoredPlayers;
    }

    public void closeConnection() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
