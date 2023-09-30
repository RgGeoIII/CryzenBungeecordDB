package fr.rggeoiii.cryzen;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

public class SanctionManager {

    private final Map<String, String> bannedPlayers = new HashMap<>();

    // Méthode pour bannir un joueur
    public void banPlayer(String playerName, String reason) {
        bannedPlayers.put(playerName, reason);

        // Récupérer l'instance du joueur
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(playerName);

        if (targetPlayer != null) {
            // Déconnecter le joueur
            targetPlayer.disconnect(new TextComponent("Vous avez été banni pour la raison : " + reason));
        }
    }

    // Méthode pour vérifier si un joueur est banni
    public boolean isPlayerBanned(String playerName) {
        return bannedPlayers.containsKey(playerName);
    }

    // Méthode pour obtenir la raison du bannissement
    public String getBanReason(String playerName) {
        return bannedPlayers.get(playerName);
    }

    // Méthode pour lever un bannissement
    public void unbanPlayer(String playerName) {
        bannedPlayers.remove(playerName);
        // Vous devrez implémenter ici la logique réelle de débannissement
    }
}