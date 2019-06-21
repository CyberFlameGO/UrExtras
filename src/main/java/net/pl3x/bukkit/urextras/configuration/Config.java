package net.pl3x.bukkit.urextras.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.urextras.UrExtras;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
    private static final String HEADER = "This is the main configuration file for UrExtras.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to UrExtras,\n"
            + "join us in our Discord.\n"
            + "\n"
            + "Discord: https://discord.gg/c4WTKms\n"
            + "Website: https://pl3x.net/ \n"
            + "Docs: https://pl3x.net/forum/view/7-urextras/ \n";
    public static boolean DEBUG_MODE;
    public static boolean COLOR_LOGS;
    public static boolean LOGGING;
    public static String LANGUAGE_FILE;

    public static int GLOBAL_COOLDOWN;

    public static boolean TREEE_SPAWNER_TOOL_CLICK;
    public static boolean TREEE_LIST_ACACIA;
    public static boolean TREEE_LIST_BIRCH;

    private static void init() {
        DEBUG_MODE = getBoolean("debug-mode",false);
        COLOR_LOGS = getBoolean("color-logs",true);
        LOGGING = getBoolean("logging", true);
        LANGUAGE_FILE = getString("language-file", "lang-en.yml");

        GLOBAL_COOLDOWN = getInt("cooldown.global", 300);

        TREEE_SPAWNER_TOOL_CLICK = getBoolean("treee-spawner-tool-click", true);
        TREEE_LIST_ACACIA = getBoolean("treee-list.acacia",true);
        TREEE_LIST_BIRCH = getBoolean("treee-list.birch",true);
    }


    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    @SuppressWarnings("deprecation")
    public static void reload() {
        UrExtras plugin = UrExtras.getInstance();
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
            ignore.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        Config.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }
}
