package net.pl3x.bukkit.urextras.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.UrExtras;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Lang {
    private static File configFile;
    private static YamlConfiguration config;

    private static final String HEADER = "This is the main language file for UrExtras.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so edit\n"
            + "with caution, and make sure you know what each string does/displays/answers/provides\n"
            + "before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to UrExtras,\n"
            + "join us in our Discord.\n"
            + "\n"
            + "Discord: https://discord.gg/c4WTKms\n"
            + "Website: https://pl3x.net/ \n"
            + "Docs: https://pl3x.net/forum/view/7-urextras/ \n";
    public static String COMMAND_NO_PERMISSION ;
    public static String COMMAND_NO_PERMISSION_PORTAL;
    public static String PLAYER_COMMAND;
    public static String DISABLED;
    public static String ON_PLAYER_DEATH;

    public static String UREXTRAS_PORTAL_INVENTORY_TITLE;
    public static String UREXTRAS_PORTAL_INVENTORY_CLOSED;
    public static String HAND_NOT_EMPTY;
    public static String GIVE_TOOL;
    public static String TOOL_DROP_ATTEMPT;
    public static String TOOL_MOVE_ATTEMPT;

    public static String NO_TREEE_SPAWNER_TOOL;
    public static String TREEE_SPAWNER_TOOL;
    public static String SET_TREEE_SPAWNER_TOOL_INFO;

    public static String TREEE_LIST_INVENTORY_TITLE;
    public static String CANNOT_SPAWN_TREEE_HERE;
    public static String TREEE_SPAWNED_PLAYERMSG;
    public static String TREEE_SPAWNED_ACACIA;
    public static String TREEE_SPAWNED_ACACIANO;
    public static String TREEE_SPAWNED_BIRCH;
    public static String TREEE_SPAWNED_BIRCHNO;
    public static String TREEE_SPAWNED_SPRUCE;
    public static String TREEE_SPAWNED_SPRUCENO;
    public static String TREEE_SPAWNED_JUNGLE;
    public static String TREEE_SPAWNED_JUNGLENO;
    public static String TREEE_SPAWNED_OAK;
    public static String TREEE_SPAWNED_OAKNO;
    public static String TREEE_SPAWNED_DARK_OAK;
    public static String TREEE_SPAWNED_DARK_OAKNO;
    public static String TREEE_SPAWNED_JUNGLE_SMALL;
    public static String TREEE_SPAWNED_JUNGLE_SMALLNO;
    public static String TREEE_SPAWNED_BIRCH_TALL;
    public static String TREEE_SPAWNED_BIRCH_TALLNO;
    public static String TREEE_SPAWNED_COCOA;
    public static String TREEE_SPAWNED_COCOANO;
    public static String TREEE_SPAWNED_CHORUS_PLANT;
    public static String TREEE_SPAWNED_CHORUS_PLANTNO;
    public static String TREEE_SPAWNED_SWAMP;
    public static String TREEE_SPAWNED_SWAMPNO;
    public static String TREEE_SPAWNED_BIG_OAK;
    public static String TREEE_SPAWNED_BIG_OAKNO;
    public static String TREEE_SPAWNED_JUNGLE_BUSH;
    public static String TREEE_SPAWNED_JUNGLE_BUSHNO;


    public static String TREEE_SPAWNED_LORE_ACACIA;
    public static String TREEE_SPAWNED_LORE_BIRCH;
    public static String TREEE_SPAWNED_LORE_SPRUCE;
    public static String TREEE_SPAWNED_LORE_JUNGLE;
    public static String TREEE_SPAWNED_LORE_OAK;
    public static String TREEE_SPAWNED_LORE_DARK_OAK;
    public static String TREEE_SPAWNED_LORE_JUNGLE_SMALL;
    public static String TREEE_SPAWNED_LORE_BIRCH_TALL;
    public static String TREEE_SPAWNED_LORE_COCOA;
    public static String TREEE_SPAWNED_LORE_CHORUS_PLANT;
    public static String TREEE_SPAWNED_LORE_SWAMP;
    public static String TREEE_SPAWNED_LORE_BIG_OAK;
    public static String TREEE_SPAWNED_LORE_JUNGLE_BUSH;

    /**
     * UrExtras Language File
     * <p>
     * Default language file (English)
     */
    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", "&4You do not have permission for that command!");
        COMMAND_NO_PERMISSION_PORTAL = getString("command-no-permission-portal", "&cYou do not have permission to use the&4 {getClicked}&c!");
        PLAYER_COMMAND = getString("player-command", "&4This command is only available to players!");
        DISABLED = getString("disabled","&cThe {getDisabledName} &cis disabled.");
        ON_PLAYER_DEATH = getString("on-player-death","&7Your &2{getToolName}&7 was removed since you died.");

        UREXTRAS_PORTAL_INVENTORY_TITLE = getString("urextras-portal-inventory-title", "UrExtras Portal");
        UREXTRAS_PORTAL_INVENTORY_CLOSED = getString("urextras-portal-inventory-closed","&7You closed &4{getInventoryName}&7.");
        HAND_NOT_EMPTY = getString("hand-not-empty","&dPlease empty your hand before clicking the &7{getClicked}&d again.");
        GIVE_TOOL = getString("give-tool","&7You received a &2{getToolName}&7.");
        TOOL_DROP_ATTEMPT = getString("tool-drop-attempt","&cYou cannot drop the &r{getToolName} &cout of your inventory.");
        TOOL_MOVE_ATTEMPT  = getString("tool-move-attempt","&cYou cannot move the &r(getToolName}&c.");

        NO_TREEE_SPAWNER_TOOL = getString("no-treee-spawner-tool","&4Treee Spawner Tool");
        TREEE_SPAWNER_TOOL = getString("treee-spawner-tool","&2Treee Spawner Tool");
        SET_TREEE_SPAWNER_TOOL_INFO = getString("set-treee-spawner-tool-info","&7Spawn Any treee.");

        TREEE_LIST_INVENTORY_TITLE = getString("treee-list-inventory-title","Treee List");
        CANNOT_SPAWN_TREEE_HERE = getString("cannot-spawn-treee-here","\n\n&5=====================================================\n" +
                                                                                    "&7You cannot use the &4{getToolName} &7here.\n" +
                                                                                    "\n" +
                                                                                    "&7Please click on one of the following materials:\n" +
                                                                                    "  &ddirt&6, &dCoarse Dirt&6, &dGrass Block&6, &dPodzol&6." +
                                                                                    "\n&5=====================================================\n\n");
        TREEE_SPAWNED_PLAYERMSG = getString("treee-spawned.playerMsg","&7You spawned a {getSpawnedName}&7.");
        TREEE_SPAWNED_ACACIA = getString("treee-spawned.acacia","&aAcacia Treee");
        TREEE_SPAWNED_ACACIANO = getString("treee-spawned.acaciaNo","&4Acacia Treee");
        TREEE_SPAWNED_BIRCH = getString("treee-spawned.birch","&aBirch Treee");
        TREEE_SPAWNED_BIRCHNO = getString("treee-spawned.birchNo","&4Birch Treee");
        TREEE_SPAWNED_SPRUCE = getString("treee-spawned.spruce","&aSpruce Treee");
        TREEE_SPAWNED_SPRUCENO = getString("treee-spawned.spruceNo","&4Spruce Treee");
        TREEE_SPAWNED_JUNGLE = getString("treee-spawned.jungle","&aJungle Treee");
        TREEE_SPAWNED_JUNGLENO = getString("treee-spawned.jungleNo","&4Jungle Treee");
        TREEE_SPAWNED_OAK = getString("treee-spawned.oak","&aOak Treee");
        TREEE_SPAWNED_OAKNO = getString("treee-spawned.oakNo","&4Oak Treee");
        TREEE_SPAWNED_DARK_OAK = getString("treee-spawned.dark-oak","&aDark Oak Treee");
        TREEE_SPAWNED_DARK_OAKNO = getString("treee-spawned.dark-oakNo","&4Dark Oak Treee");
        TREEE_SPAWNED_JUNGLE_SMALL = getString("treee-spawned.jungleSmall","&aJungle Small Treee");
        TREEE_SPAWNED_JUNGLE_SMALLNO = getString("treee-spawned.jungleSmallNo","&4Jungle Small Treee");
        TREEE_SPAWNED_BIRCH_TALL = getString("treee-spawned.birchTall","&aBirch Tall Treee");
        TREEE_SPAWNED_BIRCH_TALLNO = getString("treee-spawned.birchTallNo","&4Birch Tall Treee");
        TREEE_SPAWNED_COCOA = getString("treee-spawned.cocoa","&aCocoa Treee");
        TREEE_SPAWNED_COCOANO = getString("treee-spawned.cocoaNo","&4Cocoa Treee");
        TREEE_SPAWNED_CHORUS_PLANT = getString("treee-spawned.chorusPlant","&aChorus Plant");
        TREEE_SPAWNED_CHORUS_PLANTNO = getString("treee-spawned.chorusPlantNo","&4Chorus Plant");
        TREEE_SPAWNED_SWAMP = getString("treee-spawned.swamp","&aSwamp Treee");
        TREEE_SPAWNED_SWAMPNO = getString("treee-spawned.swampNo","&4Swamp Treee");
        TREEE_SPAWNED_BIG_OAK = getString("treee-spawned.bigOak","&aBig Oak Treee");
        TREEE_SPAWNED_BIG_OAKNO = getString("treee-spawned.bigOak","&4Big Oak Treee");
        TREEE_SPAWNED_JUNGLE_BUSH = getString("treee-spawned.jungleBush","&aJungle Bush");
        TREEE_SPAWNED_JUNGLE_BUSHNO = getString("treee-spawned.jungleBush","&4Jungle Bush");

        TREEE_SPAWNED_LORE_ACACIA = getString("treee-spawned-lore.acacia","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_BIRCH = getString("treee-spawned-lore.birch", "&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_SPRUCE = getString("treee-spawned-lore.spruce", "&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_JUNGLE = getString("treee-spawned-lore.jungle", "&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_OAK = getString("treee-spawned-lore.oak","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_DARK_OAK = getString("treee-spawned-lore.darkOak","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_JUNGLE_SMALL = getString("treee-spawned-lore.jungleSmall", "&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_BIRCH_TALL = getString("treee-spawned-lore.birchTall","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_COCOA = getString("treee-spawned-lore.cocoa","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_CHORUS_PLANT = getString("treee-spawned-lore.chorusPlant","&7Click here to spawn;&7the Chorus Plant.;;&7Chorus Plant can only;&7spawn on End Stone.;;&7After the plant is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_SWAMP = getString("treee-spawned-lore.swamp","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_BIG_OAK = getString("treee=spawned-lore.bigOak","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
        TREEE_SPAWNED_LORE_JUNGLE_BUSH = getString("treee-spawned-lore.jungleBush","&7Click here to spawn;&7the tree type.;;&7After the tree is;&7spawned your effects;&7will be removed.");
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the language file
     */
    @SuppressWarnings("deprecation")
    public static void reload() {
        UrExtras plugin = UrExtras.getInstance();
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), Config.LANGUAGE_FILE);
        }

        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException ignore) {
            if (Config.DEBUG_MODE) {
                Logger.debug("onLangReload | IOException occurred, printStackTrace() below.");
                ignore.printStackTrace();
            }
        } catch (InvalidConfigurationException ex) {
            if (Config.DEBUG_MODE) {
                Logger.debug("onLangReload | InvalidConfigurationException occurred, printStackTrace() below.");
                ex.printStackTrace();
            }
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + Config.LANGUAGE_FILE + ", please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        Lang.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    /**
     * Gets the string path
     *
     * @param path String path
     * @param def Path message
     * @return Path and message
     */
    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (recipient != null) {
            for (String part : colorize(message).split("\n")) {
                recipient.sendMessage(part);
            }
        }
    }

    /**
     * Broadcast a message to server
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        for (String part : colorize(message).split("\n")) {
            Bukkit.getOnlinePlayers().forEach(recipient -> recipient.sendMessage(part));
            Bukkit.getConsoleSender().sendMessage(part);
        }
    }

    /**
     * Colorize a String
     *
     * @param str String to colorize
     * @return Colorized String
     */
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }
}
