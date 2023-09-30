package fr.rggeoiii.cryzen;

import java.util.UUID;

public class Mute {
    private UUID playerId;
    private String playerName;
    private String reason;
    private long expirationTime;

    public Mute(String playerName, String reason, long expirationTime) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.reason = reason;
        this.expirationTime = expirationTime;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getReason() {
        return reason;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }
}
