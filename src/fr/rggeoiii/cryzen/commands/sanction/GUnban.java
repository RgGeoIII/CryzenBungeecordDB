package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.BanManager;
import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUnban extends Command implements TabExecutor {

    public static final String GUNBAN_PERMISSION = "cryzen.gunban";
    private final BanManager banManager;

    public GUnban(BanManager banManager) {
        super("gunban", GUNBAN_PERMISSION);
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
        if (args.length < 1) {
            sender.sendMessage("Utilisation incorrecte de la commande. Utilisation : /gunban <pseudo> [raison]");
            return;
        }

        String playerName = args[0];
        String reason = (args.length > 1) ? String.join(" ", args).substring(playerName.length() + 1) : "";

        if (banManager.isPlayerBanned(playerName)) {
            // Le joueur est banni, effectuez l'opération de déban
            banManager.unbanPlayer(playerName);

            // Envoyer un message formaté dans le chat du personnel (staff chat)

            String staffMessage = "[Cryzen - Staff] Tentative de débannir " + playerName + " par " + sender.getName() + " pour " + reason + ", mais le joueur n'est pas banni.";
            for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                    staffMember.sendMessage(new TextComponent(staffMessage));
                }
            }
        } else {
            // Envoyer un message formaté dans le chat du personnel (staff chat)
            String staffMessage = "[Cryzen - Staff] Le joueur " + playerName + " a été débanni par " + sender.getName() + " pour " + reason + ".";
            for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                if (staffMember.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) {
                    staffMember.sendMessage(new TextComponent(staffMessage));
                }
            }
        }
    }
}
