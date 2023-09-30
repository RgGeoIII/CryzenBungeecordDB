package fr.rggeoiii.cryzen;

public class BanData {

    private final String playerName;
    private final String bannerName;
    private final String banReason;
    private final long endTime;

    public BanData(String playerName, String bannerName, String banReason, long endTime) {
        this.playerName = playerName;
        this.bannerName = bannerName;
        this.banReason = banReason;
        this.endTime = endTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getBannerName() {
        return bannerName;
    }

    public String getBanReason() {
        return banReason;
    }

    public long getEndTime() {
        return endTime;
    }
}