package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.commands.StaffChat;
import fr.rggeoiii.cryzen.references.DataBase;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GBan extends Command implements TabExecutor {

    public static final String GBAN_PERMISSION = "cryzen.gban";
    private final DataBase dataBase;

    public GBan(DataBase dataBase) {
        super("gban", GBAN_PERMISSION);
        this.dataBase = dataBase;
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
            sender.sendMessage(new TextComponent("Cette commande ne peut être exécutée que par un joueur."));
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(new TextComponent("Utilisation incorrecte de la commande. Utilisation : /gban <pseudo> <raison>"));
            return;
        }

        String targetPlayerName = args[0];
        String reason = args[1];

        // Obtenir l'UUID du joueur cible par son nom
        UUID targetPlayerId = dataBase.getPlayerUUIDByName(targetPlayerName);

        if (targetPlayerId == null) {
            sender.sendMessage(new TextComponent("Le joueur " + targetPlayerName + " n'existe pas."));
            return;
        }

        if (dataBase.isPlayerBanned(targetPlayerName)) {
            sender.sendMessage(new TextComponent("Le joueur " + targetPlayerName + " est déjà banni."));
            return;
        }

        // Utilisation de la méthode banPlayer de DataBase pour bannir le joueur par son nom
        long endTime = -1L; // Bannissement permanent
        String bannerName = sender.getName();

        // Stocker la sanction dans la base de données
        dataBase.storeBan(targetPlayerName, targetPlayerId, bannerName, reason);

        // Vous pouvez également déconnecter immédiatement le joueur s'il est en ligne
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
        if (targetPlayer != null) {
            targetPlayer.disconnect(new TextComponent("Vous avez été banni définitivement par " + bannerName + " pour la raison suivante: " + reason));
        }

        // Envoyer un message formaté dans le chat du personnel (staff chat)
        String staffMessage = "[Cryzen - Staff] " + targetPlayerName + " a été banni par " + bannerName + " pour : " + reason;
        for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
            if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                staffMember.sendMessage(new TextComponent(staffMessage));
            }
        }
    }
}
