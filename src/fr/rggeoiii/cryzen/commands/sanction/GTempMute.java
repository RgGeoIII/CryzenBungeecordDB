package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.MuteManager;
import fr.rggeoiii.cryzen.commands.OriginalMessage;
import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GTempMute extends Command implements TabExecutor {

    private final MuteManager muteManager;
    public static final String GTEMPMUTE_PERMISSION = "cryzen.gtempmute";

    public GTempMute(MuteManager muteManager) {
        super("gtempmute", GTEMPMUTE_PERMISSION);
        this.muteManager = muteManager;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Cette commande ne peut être utilisée que par un joueur."));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(new TextComponent("Utilisation incorrecte de la commande. Utilisation : /gtempmute <pseudo> <durée> <raison>"));
            return;
        }

        String targetPlayerName = args[0];
        long durationInSeconds;

        try {
            durationInSeconds = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponent("Durée invalide. Utilisation : /gtempmute <pseudo> <durée> <raison>"));
            return;
        }

        if (durationInSeconds <= 0) {
            sender.sendMessage(new TextComponent("La durée doit être supérieure à zéro. Utilisation : /gtempmute <pseudo> <durée> <raison>"));
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(new TextComponent("Le joueur " + targetPlayerName + " n'est pas en ligne."));
            return;
        }

        UUID targetPlayerId = targetPlayer.getUniqueId();

        if (muteManager.isPlayerMuted(targetPlayerId)) {
            sender.sendMessage(new TextComponent("Le joueur " + targetPlayerName + " est déjà muté."));

            // Utilisez OriginalMessageChat pour envoyer le message original
            OriginalMessage.sendOriginalMessage(targetPlayer, reason);
            return;
        }

        // Convertir la durée en millisecondes
        long durationInMillis = durationInSeconds * 1000;

        // Calculer l'heure d'expiration
        long expirationTime = System.currentTimeMillis() + durationInMillis;

        muteManager.addMute(targetPlayerId, targetPlayerName, reason, expirationTime);

        // Envoyer un message formaté dans le chat du personnel (staff chat)
        String staffMessage = "[Cryzen - Staff] " + targetPlayerName + " a été muté par " + sender.getName() + " pour " + reason + " pour une durée de " + durationInSeconds + " secondes.";

        for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
            if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                staffMember.sendMessage(new TextComponent(staffMessage));
            }
        }

        // Planifier le démutage après la durée spécifiée
        ProxyServer.getInstance().getScheduler().schedule(muteManager.getPlugin(), () -> {
            if (muteManager.isPlayerMuted(targetPlayerId)) {
                muteManager.removeMute(targetPlayerId);

                // Envoyer un message formaté dans le chat du personnel (staff chat) pour le démutage
                String unmuteMessage = "[Cryzen - Staff] Le mute de " + targetPlayerName + " a expiré.";
                for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                    if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                        staffMember.sendMessage(new TextComponent(unmuteMessage));
                    }
                }
            }
        }, durationInSeconds, TimeUnit.SECONDS);
    }
}
