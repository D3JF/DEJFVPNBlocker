package cf.dejf.DEJFVPNBlocker;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.io.IOException;

public class PlayerJoinListener extends PlayerListener {

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerIp = "0.0.0.0";

        if(player.getAddress().getHostName() != null) {
            playerIp = player.getAddress().getHostName();
        }

        System.out.println("[DEJFVPNBlocker] Player " + player.getName() + " is trying to join from IP " + playerIp);

        try {
            if(HTTPRequester.isVPN(playerIp)) {
                System.out.println("[DEJFVPNBlocker] This player is on a VPN!");
                if(DEJFVPNBlocker.whitelistedIps.contains(playerIp)) {
                    System.out.println("[DEJFVPNBlocker] This player will not be kicked as their IP has been whitelisted in the plugin configuration.");
                } else {
                    player.kickPlayer(DEJFVPNBlocker.kickMessage);
                }
            } else {
                System.out.println("[DEJFVPNBlocker] This player is most likely not on a VPN.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
