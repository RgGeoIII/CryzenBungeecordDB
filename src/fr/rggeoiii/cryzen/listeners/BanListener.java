package fr.rggeoiii.cryzen.listeners;

import fr.rggeoiii.cryzen.references.DataBase;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BanListener implements Listener {

    private final DataBase dataBase;

    public BanListener(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        String playerName = event.getConnection().getName();

        // Vérifier si le joueur est dans la base de données et banni
        if (isPlayerBannedInDatabase(playerName)) {
            event.setCancelled(true);
            event.setCancelReason("Vous avez été banni de ce serveur.");
        }
    }

    private boolean isPlayerBannedInDatabase(String playerName) {
        try (Connection connection = dataBase.getConnection()) {
            String query = "SELECT 1 FROM bans WHERE player_name = ? AND ban_date IS NOT NULL";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next(); // Retourne true si le joueur est banni dans la base de données
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur, assumez que le joueur n'est pas banni
        }
    }
}
