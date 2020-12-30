package cf.dejf.DEJFVPNBlocker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
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
    public static String kickMessage;

    @Override
    public void onEnable() {

        plugin = this;
        instance = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Loading plugin version " + pdf.getVersion());

        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerJoinListener(), Event.Priority.Normal, this);
        this.getCommand("vpnblocker").setExecutor(this);
        ConfigManager.load("config");

        log.info("[" + pluginName + "] loaded successfully! " + pdf.getVersion());

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
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker check " + ChatColor.GREEN + "<IP>"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Check if IP is a VPN");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker whitelist " + ChatColor.GREEN + "<IP>"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Add IP to whitelist");
                    sender.sendMessage(ChatColor.AQUA + "/vpnblocker unwhitelist " + ChatColor.GREEN + "<IP>"
                            + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Remove IP from whitelist");
                    return true;
                }

                if(args[0].equalsIgnoreCase("check")) {
                    String ip = args[1];
                    if(!ip.matches("\\d+(\\.\\d+)*")) { // Match numbers and dots
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        try {
                            if(HTTPRequester.isVPN(ip)) {
                                sender.sendMessage(ChatColor.DARK_AQUA + "The IP " + ChatColor.AQUA + ip
                                        + ChatColor.DARK_AQUA + " appears to be a VPN connection!");
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "The IP " + ChatColor.AQUA + ip
                                        + ChatColor.DARK_AQUA + " most likely isn't a VPN.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(args[0].equalsIgnoreCase("whitelist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d+(\\.\\d+)*")) { // Match numbers and dots
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        whitelistedIps.add(ip);
                        ConfigManager.save("config");
                        sender.sendMessage(ChatColor.DARK_AQUA + "Added " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " to whitelist!");
                    }
                }

                if(args[0].equalsIgnoreCase("unwhitelist")) {
                    String ip = args[1];
                    if(!ip.matches("\\d+(\\.\\d+)*")) { // Match numbers and dots
                        sender.sendMessage(ChatColor.DARK_AQUA + "Invalid IP!");
                    } else {
                        whitelistedIps.remove(ip);
                        ConfigManager.save("config");
                        sender.sendMessage(ChatColor.DARK_AQUA + "Removed " + ChatColor.AQUA + ip + ChatColor.DARK_AQUA + " from whitelist!");
                    }
                }

            }
        }



        return true;
    }

}
