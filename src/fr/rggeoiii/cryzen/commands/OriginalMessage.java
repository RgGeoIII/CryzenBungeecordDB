package fr.rggeoiii.cryzen.commands;

import fr.rggeoiii.cryzen.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.chat.TextComponent;

public class OriginalMessage extends Command {
    public OriginalMessage() {
        super("originalmessage", "cryzen.om"); // Définissez le nom de la commande et la permission ici
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Vous n'avez pas besoin d'implémenter une action ici car la logique d'envoi de message
        // original a déjà été définie dans la méthode sendOriginalMessage.
    }

    public static void sendOriginalMessage(CommandSender sender, String message) {
        // Créez un message personnalisé pour le canal Original Message
        TextComponent originalMessage = new TextComponent("[Original message] " + sender.getName() + ": " + message);

        // Envoyez le message aux joueurs avec la permission cryzen.om
        for (CommandSender recipient : Main.getInstance().getProxy().getPlayers()) {
            if (recipient.hasPermission("cryzen.om")) {
                recipient.sendMessage(originalMessage);
            }
        }
    }
}
