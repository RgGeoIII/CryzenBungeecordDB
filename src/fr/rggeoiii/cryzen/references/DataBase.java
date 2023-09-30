package fr.rggeoiii.cryzen.references;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DataBase {

    private Plugin plugin;
    private Configuration config;

    public Connection getConnection() throws SQLException {
        String dbUrl = getDbUrl();
        String dbUsername = getDbUsername();
        String dbPassword = getDbPassword();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Charger le driver MySQL
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public void storeBan(String playerName, UUID playerUUID, String bannerName, String reason, long endTime) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO bans (player_uuid, banner_uuid, reason, ban_date, player_name) VALUES (?, ?, ?, NOW(), ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, bannerName);
                statement.setString(3, reason);
                statement.setString(4, playerName);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public DataBase(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir(); // Crée le dossier du plugin s'il n'existe pas
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // Si le fichier de configuration n'existe pas, copiez-le depuis les ressources du plugin
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Chargez le fichier de configuration YAML
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDbUrl() {
        return config.getString("database.url");
    }

    public String getDbUsername() {
        return config.getString("database.username");
    }

    public String getDbPassword() {
        return config.getString("database.password");
    }
}
