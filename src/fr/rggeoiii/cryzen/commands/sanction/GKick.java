package fr.rggeoiii.cryzen.commands.sanction;

import fr.rggeoiii.cryzen.commands.StaffChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GKick extends Command implements TabExecutor {

    public static final String GKICK_PERMISSION ="cryzen.gkick";

    public GKick() {
        super("gkick", GKICK_PERMISSION);
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
        if (args.length < 2) {
            sender.sendMessage("Utilisation incorrecte de la commande. Utilisation : /gkick <pseudo> <raison>");
            return;
        }

        String targetPlayerName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            sender.sendMessage("Le joueur " + targetPlayerName + " n'est pas en ligne.");
            return;
        }

        // Obtenez le pseudo du modérateur qui exécute la commande
        String moderatorName = sender.getName();

        // Kick le joueur
        //targetPlayer.disconnect(new TextComponent("Vous avez été kick par " + moderatorName + " pour : " + reason));
        targetPlayer.connect(ProxyServer.getInstance().getServerInfo("lobby"));
        targetPlayer.sendMessage(new TextComponent("Vous avez été kick par " + sender.getName() + " pour: " + reason ));


// Envoyer un message formaté dans le chat du personnel
        String staffMessage = "[Cryzen - Staff] " + targetPlayerName + " a été kick par " + moderatorName + " pour : " + reason;
        ProxyServer.getInstance().getPlayers().stream()
                .filter(player -> player.hasPermission(StaffChat.STAFF_CHAT_PERMISSION)) // Assurez-vous que la permission cryzen.staffchat est accordée aux membres du personnel
                .forEach(player -> player.sendMessage(new TextComponent(staffMessage)));


    }
}