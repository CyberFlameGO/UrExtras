package net.pl3x.bukkit.urextras;

import net.pl3x.bukkit.urextras.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {
    private static void log(String msg){
        msg = ChatColor.translateAlternateColorCodes('&', "&a[&7UrExtras&a]&r " + msg);
        if (!Config.COLOR_LOGS){
            msg = ChatColor.stripColor(msg);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg){
        msg = ChatColor.translateAlternateColorCodes('&', "&2[&eDEBUG&2]&r " + msg);

        if (Config.DEBUG_MODE){
            if (!Config.COLOR_LOGS){
                msg = ChatColor.stripColor(msg);
            }
            log(msg);
        }
    }

    public static void info(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&b[&7INFO&b]&f " + msg);
        if (Config.LOGGING) {
            if (!Config.COLOR_LOGS){
                msg = ChatColor.stripColor(msg);
            }
            log(msg);
        }
    }

    public static void warn(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&6[&eWARN&6]&a " + msg);
        if (Config.LOGGING){
            if (!Config.COLOR_LOGS){
                msg = ChatColor.stripColor(msg);
            }
            log(msg);
        }

    }

    public static void error(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&4[&cERROR&4]&e " + msg);
        if (Config.LOGGING){
            if (!Config.COLOR_LOGS){
                msg = ChatColor.stripColor(msg);
            }
            log(msg);
        }
    }
}

