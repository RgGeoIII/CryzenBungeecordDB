package fr.rggeoiii.cryzen;

class MuteData {
    private final String playerName;
    private final String reason;
    private final long endTime;

    public MuteData(String playerName, String reason, long endTime) {
        this.playerName = playerName;
        this.reason = reason;
        this.endTime = endTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getReason() {
        return reason;
    }

    public long getEndTime() {
        return endTime;
    }
}