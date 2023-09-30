package fr.rggeoiii.cryzen;

import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BanManager {

    private final Map<String, BanData> bannedPlayers = new HashMap<>();

    public String formatDuration(long durationMilliseconds) {
        long days = TimeUnit.MILLISECONDS.toDays(durationMilliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMilliseconds) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMilliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMilliseconds) % 60;

        if (days > 0) {
            return days + " jour(s), " + hours + " heure(s), " + minutes + " minute(s), " + seconds + " seconde(s)";
        } else if (hours > 0) {
            return hours + " heure(s), " + minutes + " minute(s), " + seconds + " seconde(s)";
        } else if (minutes > 0) {
            return minutes + " minute(s), " + seconds + " seconde(s)";
        } else {
            return seconds + " seconde(s)";
        }
    }

    public void banPlayer(String playerName, String bannerName, String reason, long duration, String moderatorName) {
        // Votre logique pour bannir le joueur avec la durée spécifiée
        long endTime = duration > 0 ? System.currentTimeMillis() + duration : -1L;
        BanData banData = new BanData(playerName, bannerName, reason, endTime);
        bannedPlayers.put(playerName, banData);

        // Vous pouvez également déconnecter immédiatement le joueur s'il est en ligne
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(playerName);
        if (targetPlayer != null) {
            if (duration > 0) {
                // Le bannissement est temporaire, inclure le temps restant avant le débannissement
                long remainingTimeMillis = endTime - System.currentTimeMillis();
                String remainingTimeString = formatDuration(remainingTimeMillis);
                String endTimeString = formatEndTime(endTime);
                targetPlayer.disconnect(new TextComponent(" Vous avez été temporairement banni par " + moderatorName + " pendant " + formatDuration(duration) + " pour la raison : " + reason + ". Vous serez débanni le " + endTimeString));
            } else {
                // Le bannissement est permanent
                targetPlayer.disconnect(new TextComponent(" Vous avez été définitivement banni par " + bannerName + " pour la raison : " + reason));
            }
        }
    }

    public String formatEndTime(long endTime) {
        if (endTime == -1) {
            return "jamais"; // Bannissement permanent
        } else {
            long durationMillis = endTime - System.currentTimeMillis();
            long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;

            if (days > 0) {
                return days + " jour(s), " + hours + " heure(s), " + minutes + " minute(s), " + seconds + " seconde(s) restants";
            } else if (hours > 0) {
                return hours + " heure(s), " + minutes + " minute(s), " + seconds + " seconde(s) restantes";
            } else if (minutes > 0) {
                return minutes + " minute(s), " + seconds + " seconde(s) restantes";
            } else {
                return seconds + " seconde(s) restantes";
            }
        }
    }

    public boolean tempBanPlayer(String playerName, long endTime, String bannerName, String reason) {
        if (isPlayerBanned(playerName)) {
            // Le joueur est déjà banni, ne faites rien ici
            return false;
        }

        BanData banData = new BanData(playerName, bannerName, reason, endTime);
        bannedPlayers.put(playerName, banData);
        return true;
    }

    public boolean isPlayerBanned(String playerName) {
        if (bannedPlayers.containsKey(playerName)) {
            BanData banData = bannedPlayers.get(playerName);
            long endTime = banData.getEndTime();
            if (endTime == -1 || endTime > System.currentTimeMillis()) {
                return true; // Le joueur est banni jusqu'à la fin du temps ou une date ultérieure
            } else {
                // Le bannissement a expiré, nous pouvons le supprimer de la liste
                unbanPlayer(playerName); // Débannir le joueur
                System.out.println("DEBUG: Le joueur " + playerName + " était banni mais le bannissement a expiré.");
            }
        }
        System.out.println("DEBUG: Le joueur " + playerName + " n'est pas banni.");
        return false;
    }

    public long getBanEndTime(String playerName) {
        if (bannedPlayers.containsKey(playerName)) {
            return bannedPlayers.get(playerName).getEndTime();
        }
        return -1L;
    }

    public void unbanPlayer(String playerName) {
        if (bannedPlayers.containsKey(playerName)) {
            bannedPlayers.remove(playerName);

            // Envoyer un message dans le chat du personnel (staff chat) lorsque le bannissement est levé
            String staffMessage = "[Cryzen - Staff] Le joueur " + playerName + " a été débanni.";
            //for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
            //    if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
            //        staffMember.sendMessage(new TextComponent(staffMessage));
            //    }
            //}
        }
    }
    public UUID getPlayerUUIDByName(String playerName) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        return null;
    }

    public String getBannerName(String playerName) {
        if (bannedPlayers.containsKey(playerName)) {
            return bannedPlayers.get(playerName).getBannerName();
        }
        return null;
    }

    public String getBanReason(String playerName) {
        if (bannedPlayers.containsKey(playerName)) {
            return bannedPlayers.get(playerName).getBanReason();
        }
        return null;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Utilisation incorrecte de la commande. Utilisation : /gunban <pseudo> [raison]");
            return;
        }

        String playerName = args[0];
        String reason = (args.length > 1) ? String.join(" ", args).substring(playerName.length() + 1) : "";

        if (isPlayerBanned(playerName)) {
            // Le joueur est banni, effectuez l'opération de déban
            unbanPlayer(playerName);

            sender.sendMessage("Le joueur " + playerName + " a été débanni.");
            sender.sendMessage("Raison du déban : " + reason);

            // Envoyer un message formaté dans le chat du personnel (staff chat)
            String staffMessage = "[Cryzen - Staff] Le joueur " + playerName + " a été débanni par " + sender.getName() + " pour " + reason + ".";
            for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                    staffMember.sendMessage(new TextComponent(staffMessage));
                }
            }
        } else {
            sender.sendMessage("Le joueur " + playerName + " n'est pas banni.");
        }
    }
}
