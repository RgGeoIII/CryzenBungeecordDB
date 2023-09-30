package fr.rggeoiii.cryzen;

import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class MuteManager {
    private final Plugin plugin;
    private final Map<UUID, Mute> muteDataMap = new HashMap<>();
    private final Map<String, UUID> playerNameToUUID = new HashMap<>();

    public MuteManager(Plugin plugin) {
        this.plugin = plugin;
    }


    public void addMute(UUID playerId, String playerName, String reason) {
        muteDataMap.put(playerId, new Mute(playerName, reason, -1)); // -1 pour un mute permanent
    }

    public void addMute(UUID playerId, String playerName, String reason, long expirationTime) {
        muteDataMap.put(playerId, new Mute(playerName, reason, expirationTime));
    }

    public void checkExpiredMutes() {
        // Parcourez la liste des mutes et supprimez ceux qui ont expiré
        for (UUID playerId : new HashSet<>(muteDataMap.keySet())) {
            Mute mute = muteDataMap.get(playerId);
            if (mute.isExpired()) {
                removeMute(playerId);
            }
        }
    }

    public void removeMute(UUID playerId) {
        muteDataMap.remove(playerId);
    }

    public boolean isPlayerMuted(UUID playerId) {
        return muteDataMap.containsKey(playerId);
    }

    public String getMuteReason(UUID playerId) {
        if (muteDataMap.containsKey(playerId)) {
            Mute mute = muteDataMap.get(playerId);
            return mute.getReason();
        }
        return null;
    }

    public UUID getPlayerUUIDByName(String playerName) {
        return playerNameToUUID.get(playerName.toLowerCase());
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
