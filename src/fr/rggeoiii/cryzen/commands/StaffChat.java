package fr.rggeoiii.cryzen.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChat extends Command {
    public static final String STAFF_CHAT_PERMISSION ="cryzen.sc";

    public StaffChat() {
        super("sc", STAFF_CHAT_PERMISSION);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponent("Utilisation incorrecte de la commande. Utilisation : /sc <message>"));
            return;
        }

        String message = String.join(" ", args);

        // Vérifiez si l'expéditeur est un joueur et a la permission de parler dans le staff chat
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.hasPermission(STAFF_CHAT_PERMISSION)) {
                player.sendMessage(new TextComponent("Vous n'avez pas la permission de parler dans le staff chat."));
                return;
            }
        }

        // Envoyez le message dans le staff chat avec le préfixe "[Cryzen Staff]"
        String senderName = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getName() : "Console";
        String staffChatMessage = "[Cryzen Staff] " + senderName + ": " + message;

        // Envoyez le message à tous les membres du personnel qui ont la permission
        for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
            if (staffMember.hasPermission(STAFF_CHAT_PERMISSION)) {
                staffMember.sendMessage(new TextComponent(staffChatMessage));
            }
        }
    }
}