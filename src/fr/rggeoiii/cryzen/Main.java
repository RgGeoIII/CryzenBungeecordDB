package fr.rggeoiii.cryzen;

import fr.rggeoiii.cryzen.commands.OriginalMessage;
import fr.rggeoiii.cryzen.commands.StaffChat;
import fr.rggeoiii.cryzen.commands.sanction.*;
import fr.rggeoiii.cryzen.listeners.BanListener;
import fr.rggeoiii.cryzen.listeners.MuteListener;
import fr.rggeoiii.cryzen.references.DataBase;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {
    private BanManager banManager;
    private MuteManager muteManager;
    private DataBase dataBase; // Renommé la variable

    // Déclaration de la variable statique instance
    private static Main instance;

    // Méthode statique pour récupérer l'instance actuelle de Main
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Enregistrez l'instance actuelle
        instance = this;

        getLogger().info("[Cryzen:On]");

        // Chargez la configuration
        loadConfig();

        String dbUrl = dataBase.getDbUrl(); // Modifier pour utiliser la nouvelle variable
        String dbUsername = dataBase.getDbUsername(); // Modifier pour utiliser la nouvelle variable
        String dbPassword = dataBase.getDbPassword(); // Modifier pour utiliser la nouvelle variable

        // Initialisez le gestionnaire des bans
        banManager = new BanManager();

        // Initialisez le gestionnaire des mutes
        muteManager = new MuteManager(this);

        // Enregistrez les commandes et les écouteurs avec l'instance getProxy()
        getProxy().getPluginManager().registerCommand(this, new GBan(banManager, dataBase));
        getProxy().getPluginManager().registerCommand(this, new GTempBan(banManager));
        getProxy().getPluginManager().registerCommand(this, new GUnban(banManager));
        getProxy().getPluginManager().registerCommand(this, new GTempMute(muteManager));
        getProxy().getPluginManager().registerCommand(this, new GMute(muteManager));
        getProxy().getPluginManager().registerCommand(this, new GUnmute(muteManager));
        getProxy().getPluginManager().registerCommand(this, new GKick());
        getProxy().getPluginManager().registerCommand(this, new StaffChat());
        getProxy().getPluginManager().registerCommand(this, new OriginalMessage());

        getProxy().getPluginManager().registerListener(this, new BanListener(dataBase));
        getProxy().getPluginManager().registerListener(this, new MuteListener(muteManager));

        // Planifier la vérification des sanctions expirées toutes les 30 secondes
        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            checkExpiredMutes();
        }, 0, 30, TimeUnit.SECONDS);

        // Tester la connexion à la base de données au démarrage
        testDatabaseConnection();
    }

    private void testDatabaseConnection() {
        getLogger().info("Testing database connection...");

        Connection connection = null;
        try {
            // Essayer de se connecter à la base de données
            connection = dataBase.getConnection();

            // Si la connexion réussit, log un message de succès
            getLogger().info("Database connection test successful!");
        } catch (SQLException e) {
            // Si la connexion échoue, log un message d'erreur
            getLogger().severe("Failed to test database connection: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fermez la connexion si elle a été ouverte avec succès (et log si cela échoue)
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    getLogger().severe("Failed to close database connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("[Cryzen:Off]");
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    // Méthode pour lever automatiquement les sanctions expirées
    public void checkExpiredMutes() {
        muteManager.checkExpiredMutes();
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir(); // Crée le dossier du plugin s'il n'existe pas
        }

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // Si le fichier de configuration n'existe pas, créez-le en utilisant le fichier par défaut dans votre plugin

        }

        // Chargez le fichier de configuration YAML
        dataBase = new DataBase(this);
        dataBase.loadConfig();
    }
}
