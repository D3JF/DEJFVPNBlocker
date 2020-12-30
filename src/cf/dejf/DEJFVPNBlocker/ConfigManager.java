package cf.dejf.DEJFVPNBlocker;


import cf.dejf.DEJFVPNBlocker.DEJFVPNBlocker;
import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cf.dejf.DEJFVPNBlocker.DEJFVPNBlocker.*;

public class ConfigManager {

    public static void save(String configName) {
        Configuration config = getPluginConfig(configName + ".cfg");
        config.setProperty("whitelistedIps", whitelistedIps);
        config.save();
    }

    public static void load(String configName) {
        Configuration config = getPluginConfig(configName + ".cfg");
        List<String> whitelistedIpsLoad =(List<String>) config.getProperty("whitelistedIps");
        String kickMessageLoad = (String) config.getProperty("kickMessage");

        if(config.getProperty("whitelistedIps") == null) {
            List<String> defaultWhitelistedIps = new ArrayList<>();
            defaultWhitelistedIps.add("0.0.0.0");
            defaultWhitelistedIps.add("127.0.0.1");
            config.setProperty("whitelistedIps", defaultWhitelistedIps);
            whitelistedIps = defaultWhitelistedIps;
            config.save();
        } else {
            whitelistedIps = whitelistedIpsLoad;
        }

        if(config.getProperty("kickMessageLoad") == null) {
            String defaultKickMessage = "VPN connections aren't allowed!";
            config.setProperty("kickMessage", defaultKickMessage);
            kickMessage = defaultKickMessage;
            config.save();
        } else {
            kickMessage = kickMessageLoad;
        }



    }

    private static Map<String, Configuration> configDict = new HashMap<>();

    public static Configuration getPluginConfig(String config) {
        if (configDict.containsKey(config))
            return configDict.get(config);
        File file = new File(DEJFVPNBlocker.getInstance().getDataFolder(), config);
        Configuration c = new Configuration(file);
        c.load();
        configDict.put(config, c);
        return c;
    }

    /*
    public static Configuration getPlayerConfig(String playerName) {
        return getPluginConfig(playerName+".config");
    }
     */

    public static Object get(Configuration c, String object) {
        return c.getProperty(object);
    }

    public static void addDefault(Configuration c, String object, Object value) {
        if (c.getProperty(object) == null)
            addTo(c, object, value);
    }

    public static void addTo(Configuration c, String object, Object value) {
        c.setProperty(object, value);
        c.save();
    }

    public static boolean contains(Configuration c, String object) {
        Object s = c.getProperty(object);
        return s != null;
    }

    public static void remove(Configuration c, String object) {
        c.removeProperty(object);
        c.save();
    }
}
