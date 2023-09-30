package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.MuteManager;
import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.UUID;

public class GUnmute extends Command implements TabExecutor {
    private final MuteManager muteManager;

    public GUnmute(MuteManager muteManager) {
        super("gunmute");
        this.muteManager = muteManager;
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

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur.");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage("Utilisation incorrecte. Utilisez /gUnmute <pseudo> [raison]");
            return;
        }

        String targetPlayerName = args[0];
        UUID targetPlayerId = null;

        // Recherchez l'UUID associé au nom du joueur
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().equalsIgnoreCase(targetPlayerName)) {
                targetPlayerId = player.getUniqueId();
                break; // Nous avons trouvé le joueur, sortez de la boucle
            }
        }

        if (targetPlayerId == null) {
            sender.sendMessage("Le joueur " + targetPlayerName + " n'est pas en ligne.");
            return;
        }

        if (!this.muteManager.isPlayerMuted(targetPlayerId)) {
            sender.sendMessage(targetPlayerName + " n'est pas mute.");
            return;
        }

        String reason = args.length > 1 ? String.join(" ", args).substring(targetPlayerName.length() + 1) : "Aucune raison spécifiée";

        this.muteManager.removeMute(targetPlayerId);

        // Envoyer un message formaté dans le chat du personnel (staff chat)
        String staffMessage = "[Cryzen - Staff] " + targetPlayerName + " a été unmute par " + sender.getName() + " pour " + reason;
        for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
            if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                staffMember.sendMessage(new TextComponent(staffMessage));
            }
        }
    }
}
