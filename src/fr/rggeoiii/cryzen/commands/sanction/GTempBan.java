package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.BanManager;
import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GTempBan extends Command implements TabExecutor {

    public static final String GTEMPBAN_PERMISSION ="cryzen.gtempban";
    private final BanManager banManager;

    public GTempBan(BanManager banManager) {
        super("gtempban", GTEMPBAN_PERMISSION);
        this.banManager = banManager;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Compléter le nom du joueur ici (par exemple, en utilisant une liste de joueurs en ligne)
            // Vous pouvez personnaliser cette partie en fonction de la logique de votre plugin
            List<String> playerNames = new ArrayList<>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return Collections.emptyList(); // Aucune suggestion par défaut
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur.");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("Utilisation incorrecte. Utilisez /gtempban <pseudo> <temps en heures ou en jours> <raison>.");
            return;
        }

        String targetPlayerName = args[0];
        String durationString = args[1].toLowerCase(); // Convertissez en minuscules pour gérer "h" ou "d" indépendamment de la casse
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        ProxiedPlayer moderator = (ProxiedPlayer) sender;
        String moderatorName = moderator.getName();

        // Gérez le temps spécifié en heures ou en jours
        long durationMilliseconds;

        if (durationString.endsWith("h")) {
            // Supprimez le suffixe "h" et convertissez en heures
            int hours = Integer.parseInt(durationString.substring(0, durationString.length() - 1));
            durationMilliseconds = TimeUnit.HOURS.toMillis(hours);
        } else if (durationString.endsWith("d")) {
            // Supprimez le suffixe "d" et convertissez en jours
            int days = Integer.parseInt(durationString.substring(0, durationString.length() - 1));
            durationMilliseconds = TimeUnit.DAYS.toMillis(days);
        } else {
            sender.sendMessage("Le temps spécifié est invalide. Utilisez une valeur positive en heures (par exemple, 1h) ou en jours (par exemple, 2d).");
            return;
        }

        // Ajout de la logique pour bannir temporairement le joueur
        boolean success = banManager.tempBanPlayer(targetPlayerName, durationMilliseconds, reason, moderatorName);

        if (success) {
            long endTime = System.currentTimeMillis() + durationMilliseconds;
            String endTimeString = formatTime(endTime);

            // Vous pouvez également déconnecter immédiatement le joueur s'il est en ligne
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
            if (targetPlayer != null) {
                targetPlayer.disconnect(new TextComponent(" Vous avez été temporairement banni par " + moderatorName + " pendant " + durationString + " pour la raison : " + reason + ". Vous serez débanni le " + endTimeString));
            }

            // Envoyer un message formaté dans le chat du personnel (staff chat)
            String staffMessage = "[Cryzen - Staff] " + targetPlayerName + " a été temporairement banni par " + moderatorName + " pendant " + durationString + " pour " + reason;
            for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                    staffMember.sendMessage(new TextComponent(staffMessage));
                }
            }
        } else {
            sender.sendMessage("Le joueur " + targetPlayerName + " n'existe pas ou la durée spécifiée est invalide.");
        }
    }

    private String formatTime(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timeMillis));
    }
}