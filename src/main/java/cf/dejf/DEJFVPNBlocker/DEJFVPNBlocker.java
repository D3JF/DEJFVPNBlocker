package cf.dejf.DEJFVPNBlocker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DEJFVPNBlocker extends JavaPlugin {

    private DEJFVPNBlocker plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;

    public static DEJFVPNBlocker instance;
    public static DEJFVPNBlocker getInstance() {
        return instance;
    }
    public static List<String> whitelistedIps;
    public static List<String> blacklistedIps;
    public static HashMap<String, IPLogEntry> ipLog;
    public static String vpnKickMessage;
    public static String blacklistKickMessage;
    public static int databaseClearIntervalSeconds;

    @Override
    public void onEnable() {

        plugin = this;
        instance = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Loading plugin version " + pdf.getVersion());

        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerJoinListener(plugin), Event.Priority.Normal, this);
        this.getCommand("vpnblocker").setExecutor(this);
        ConfigManager.load();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            if(ipLog.entrySet().removeIf(e ->
                (e.getValue().getCheckDate().getTime() - new Date().getTime()) > databaseClearIntervalSeconds*1000
            )) {
                ConfigManager.saveDatabase();
            }
        }, 1200L, 1200L);

        log.info("[" + pluginName + "] loaded successfully!");

    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] unloaded successfully!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("vpnblocker")) {
            if(sender.isOp() || sender.hasPermission("dejfvpnblocker.vpnblocker")) {
                if(args.length == 0 || args.length > 2 || args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.BLUE + "DEJFVPNBlocker Help");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker help" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Display help screen");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker check " + ChatColor.GRAY + "IP"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Check if IP is a VPN");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker check " + ChatColor.GRAY + "name"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Check if a player is using a VPN");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker whitelist " + ChatColor.GRAY + "IP"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Add IP to whitelist");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker unwhitelist " + ChatColor.GRAY + "IP"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Remove IP from whitelist");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker blacklist " + ChatColor.GRAY + "IP"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Add IP to blacklist (ban IP)");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker unblacklist " + ChatColor.GRAY + "IP"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Remove IP from blacklist");
                    sender.sendMessage("Version " + pdf.getVersion());
                    return true;
                }

                if(args[0].equalsIgnoreCase("check")) {
                    String input = args[1];
                    if(!input.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { // Match IP
                        Player[] players = this.getServer().getOnlinePlayers();
                        boolean playerFound = false;
                        for(Player player : players) {
                            if(player.getName().contains(input)) {
                                String ip = player.getAddress().getHostName();
                                playerFound = true;
                                sender.sendMessage(ChatColor.DARK_AQUA + "Processing...");
                                try {
                                    if(HTTPRequester.checkIp(ip)) {
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Player " + ChatColor.AQUA + player.getName()
                                                + ChatColor.DARK_AQUA + " with IP " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA
                                                + " appears to be using a VPN!");
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Player " + ChatColor.AQUA + player.getName()
                                                + ChatColor.DARK_AQUA + " with IP " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA
                                                + " most likely isn't using a VPN.");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(!playerFound) {
                            sender.sendMessage(ChatColor.DARK_AQUA + "Player not found.");
                        }
                    } else {
                        try {
                            sender.sendMessage(ChatColor.DARK_AQUA + "Processing...");
                            if(HTTPRequester.checkIp(input)) {
                                sender.sendMessage(ChatColor.DARK_AQUA + "The IP " + ChatColor.AQUA + input
                                        + ChatColor.DARK_AQUA + " appears to be a VPN connection!");
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "The IP " + ChatColor.AQUA + input
                                        + ChatColor.DARK_AQUA + " most likely isn't a VPN.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(args[0].equalsIgnoreCase("whitelist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { // Match IP
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        whitelistedIps.add(ip);
                        ConfigManager.save();
                        sender.sendMessage(ChatColor.DARK_AQUA + "Added " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " to whitelist!");
                    }
                }

                if(args[0].equalsIgnoreCase("unwhitelist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { // Match IP
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        whitelistedIps.remove(ip);
                        ConfigManager.save();
                        sender.sendMessage(ChatColor.DARK_AQUA + "Removed " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " from whitelist!");
                    }
                }

                if(args[0].equalsIgnoreCase("blacklist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { // Match IP
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        blacklistedIps.add(ip);
                        ConfigManager.save();
                        sender.sendMessage(ChatColor.DARK_AQUA + "Added " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " to blacklist!");
                    }
                }

                if(args[0].equalsIgnoreCase("unblacklist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { // Match IP
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        blacklistedIps.remove(ip);
                        ConfigManager.save();
                        sender.sendMessage(ChatColor.DARK_AQUA + "Removed " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " from blacklist!");
                    }
                }

            }
        }



        return true;
    }

}
