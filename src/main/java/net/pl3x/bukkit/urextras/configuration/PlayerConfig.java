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

public class PlayerConfig extends YamlConfiguration {
    private static final Map<UUID, PlayerConfig> configs = new HashMap<>();

    public static CompletableFuture<PlayerConfig> getConfig(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (configs) {
                return configs.computeIfAbsent(player.getUniqueId(),
                        k -> new PlayerConfig(player.getUniqueId()));
            }
        });
    }

    public static void removeConfig(OfflinePlayer player) {
        synchronized (configs) {
            configs.remove(player.getUniqueId());
        }
    }

    private final File file;
    private final Object saveLock = new Object();
    private final UUID uuid;

    private PlayerConfig(UUID uuid) {
        super();
        File dir = new File(UrExtras.getInstance().getDataFolder(), "userdata");
        this.file = new File(dir, uuid + ".yml");
        this.uuid = uuid;
        load();
    }

    public boolean isUUID(OfflinePlayer player) {
        return this.uuid == player.getUniqueId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    private void load() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    private void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

}
