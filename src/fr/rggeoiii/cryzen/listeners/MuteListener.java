package fr.rggeoiii.cryzen.listeners;

import fr.rggeoiii.cryzen.MuteManager;
import fr.rggeoiii.cryzen.commands.OriginalMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MuteListener implements Listener {

    private final MuteManager muteManager;

    public MuteListener(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();

            if (player.hasPermission("cryzen.bypassmute")) {
                // Si le joueur a la permission "cryzen.bypassmute", il peut parler même s'il est muté
                return;
            }

            if (muteManager.isPlayerMuted(player.getUniqueId())) {
                event.setCancelled(true);

                // Envoyer le message du joueur muté dans le canal "Original Message"
                OriginalMessage.sendOriginalMessage(player, event.getMessage());

                player.sendMessage("Vous êtes actuellement muté et ne pouvez pas parler.");
            }
        }
    }
}
