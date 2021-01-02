package cf.dejf.DEJFVPNBlocker;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.io.IOException;
import java.util.Date;

import static cf.dejf.DEJFVPNBlocker.DEJFVPNBlocker.*;

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
            boolean isVPN;

            if(ipLog.containsKey(playerIp)) {
                IPLogEntry ipLogEntry = ipLog.get(playerIp);
                isVPN = ipLogEntry.isVPN();
                System.out.println("[DEJFVPNBlocker] This IP was found in the local database (i.e. it belongs to an active user).");
            } else {
                isVPN = HTTPRequester.checkIp(playerIp);
                ipLog.put(playerIp, new IPLogEntry(isVPN, new Date()));
                ConfigManager.saveDatabase();
                System.out.println("[DEJFVPNBlocker] This IP was not found in the local database (i.e. it is new or the database has cleared itself recently).");
            }

            if(isVPN) {
                System.out.println("[DEJFVPNBlocker] This player is on a VPN!");
                if(whitelistedIps.contains(playerIp)) {
                    System.out.println("[DEJFVPNBlocker] This player will not be kicked as their IP has been whitelisted in the plugin configuration.");
                } else {
                    player.kickPlayer(vpnKickMessage);
                }
            } else {
                System.out.println("[DEJFVPNBlocker] This player is most likely not on a VPN.");
            }

            if(blacklistedIps.contains(playerIp)) {
                System.out.println("[DEJFVPNBlocker] This IP has been blacklisted in the plugin configuration!");
                player.kickPlayer(blacklistKickMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
