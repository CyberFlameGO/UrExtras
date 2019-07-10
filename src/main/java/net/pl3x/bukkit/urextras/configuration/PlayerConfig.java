package net.pl3x.bukkit.urextras.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.pl3x.bukkit.urextras.UrExtras;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * UrExtras Player Configuration File
 *
 * Creates a custom file for players
 */
public class PlayerConfig extends YamlConfiguration {
    private static final Map<UUID, PlayerConfig> configs = new HashMap<>();

    /**
     * Gets the players configuration file if offline
     *
     * @param player Get offline player configuration file
     * @return player configuration file
     */
    public static CompletableFuture<PlayerConfig> getConfig(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (configs) {
                return configs.computeIfAbsent(player.getUniqueId(),
                        k -> new PlayerConfig(player.getUniqueId()));
            }
        });
    }

    /**
     * Deletes the players configuration file
     *
     * @param player Remove player configuration file
     */
    public static void removeConfig(OfflinePlayer player) {
        synchronized (configs) {
            configs.remove(player.getUniqueId());
        }
    }

    private final File file;
    private final Object saveLock = new Object();
    private final UUID uuid;

    /**
     * Player configuration file
     *
     * Check to see if player has configuration file
     * If file does not exist, create new one
     *
     * @param uuid Get players uuid
     */
    private PlayerConfig(UUID uuid) {
        super();
        File dir = new File(UrExtras.getInstance().getDataFolder(), "userdata");
        this.file = new File(dir, uuid + ".yml");
        this.uuid = uuid;
        load();
    }

    /**
     * Player uuid
     *
     * @param player Get player
     * @return Players uuid
     */
    public boolean isUUID(OfflinePlayer player) {
        return this.uuid == player.getUniqueId();
    }

    /**
     * Player
     *
     * @return Player
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Load player configuration file
     */
    private void load() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Save players configuration file
     */
    private void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

}
