package cf.dejf.DEJFVPNBlocker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import static cf.dejf.DEJFVPNBlocker.DEJFVPNBlocker.*;

public class PlayerJoinListener extends PlayerListener {
    private DEJFVPNBlocker plugin;


    public PlayerJoinListener(DEJFVPNBlocker plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final String playerIp = player.getAddress().getAddress().getHostAddress();

        System.out.println("[DEJFVPNBlocker] Player " + player.getName() + " is trying to join from IP " + playerIp);

        //Check Blacklist
        if (blacklistedIps.contains(playerIp)) {
            System.out.println("[DEJFVPNBlocker] This IP has been blacklisted in the plugin configuration!");
            player.kickPlayer(blacklistKickMessage);
            return;
        }
        if (ipLog.containsKey(playerIp)) {
            IPLogEntry ipLogEntry = ipLog.get(playerIp);
            System.out.println("[DEJFVPNBlocker] This IP was found in the local database (i.e. it belongs to an active user).");
            if (ipLogEntry.isVPN()) {
                System.out.println("[DEJFVPNBlocker] This player is on a VPN!");
                if (whitelistedIps.contains(playerIp)) {
                    System.out.println("[DEJFVPNBlocker] This player will not be kicked as their IP has been whitelisted in the plugin configuration.");
                } else {
                    player.kickPlayer(vpnKickMessage);
                }
            }
            return;
        }
        System.out.println("[DEJFVPNBlocker] This IP was not found in the local database (i.e. it is new or the database has cleared itself recently).");

        //Run the lookup Async, so the main server thread isn't frozen.
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            try {
                final boolean isVPN = HTTPRequester.checkIp(playerIp);
                //Carry out the following actions synchronously
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ipLog.put(playerIp, new IPLogEntry(isVPN, new Date()));
                    ConfigManager.saveDatabase();
                    if (isVPN) {
                        System.out.println("[DEJFVPNBlocker] This player is on a VPN!");
                        if (whitelistedIps.contains(playerIp)) {
                            System.out.println("[DEJFVPNBlocker] This player will not be kicked as their IP has been whitelisted in the plugin configuration.");
                        } else {
                            player.kickPlayer(vpnKickMessage);
                        }
                    } else {
                        System.out.println("[DEJFVPNBlocker] This player is most likely not on a VPN.");
                    }
                }, 0L);
            } catch (IOException e) {
                plugin.getServer().getLogger().log(Level.WARNING, "[DEJFVPNBlocker] Error occurred attempting to get details for IP: " + playerIp, e);
            }
        }, 0L);


    }

}
