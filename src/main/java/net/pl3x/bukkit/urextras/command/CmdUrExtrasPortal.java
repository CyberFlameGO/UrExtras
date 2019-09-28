package net.pl3x.bukkit.urextras.command;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Config;
import net.pl3x.bukkit.urextras.util.inventories.CustomInventories;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/*
 * TODO:
 *   - Add check for players gamemode
 *   - Add watermelon
 *   - Add pumpkins
 */

/**
 * UrExtras Portal
 * <p>
 * Creates a custom inventory (Barrel)
 * Inside this custom inventory will have custom tools & weapons
 */

public class CmdUrExtrasPortal implements TabExecutor, Listener {

    private UrExtras plugin;

    public CmdUrExtrasPortal(UrExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    /**
     * Creates a portal (custom inventory) for our extra features
     * <p>
     * This will hold all the features available for players to select from
     * Once a player clicks an item inside the UrExtras Portal (gui) depending on
     * the feature a new portal (custom inventory) will appear or an item will be placed inside
     * their inventory
     *
     * @param sender Checks for Player
     * @param command Create custom inventory(Barrel)
     * @param label Check for command name or aliases
     * @param args Cancel if args is present
     * @return UrExtras Portal (custom inventory)
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.urextras.portal")){
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player target = (Player) sender;

        /* INFO: Make UrExtras Portal Inventory */
        CustomInventories textInv = new CustomInventories(Lang.UREXTRAS_PORTAL_INVENTORY_TITLE, 36, event -> {
            // NOTICE: Do nothing
        },plugin);

        /* INFO: Add tools & weapons */
        List<String> appleIconLore = new ArrayList<>();
        appleIconLore.add(Lang.colorize("&7Click to close Effects") );
        appleIconLore.add(Lang.colorize("&7Portal inventory") );
        textInv.setToolOrWeapon(0, new ItemStack(Material.APPLE, 1), Lang.colorize("&4Close Inventory"), appleIconLore);

        List<String> treeeSpawnerTool = new ArrayList<>();
        treeeSpawnerTool.add(Lang.colorize("&8Click to receive your Tool."));
        treeeSpawnerTool.add(Lang.colorize(""));
        if (Config.TREEE_SPAWNER_TOOL_CLICK) {
            if (!target.hasPermission("command.urextras.portal.treeespawnertool")) {
                treeeSpawnerTool.add(Lang.colorize("&cYou do not have permission to"));
                treeeSpawnerTool.add(Lang.colorize("&cuse this feature."));
                treeeSpawnerTool.add(Lang.colorize(""));
                treeeSpawnerTool.add(Lang.colorize("&7More information coming soon."));
                Logger.debug("onCommand | No permission to treepawnertool, returned");
            } else {
                treeeSpawnerTool.add(Lang.colorize("&7When you click on a block with"));
                treeeSpawnerTool.add(Lang.colorize("&7the tool given, a custom tree list"));
                treeeSpawnerTool.add(Lang.colorize("&7inventory will appear for selection."));
                treeeSpawnerTool.add(Lang.colorize(""));
                treeeSpawnerTool.add(Lang.colorize("&7Once you select/click a treee of"));
                treeeSpawnerTool.add(Lang.colorize("&7choice, it will then spawn on the"));
                treeeSpawnerTool.add(Lang.colorize("&7block you just clicked."));
                Logger.debug("onCommand | Target was given a Tree Spawner Tool.");
            }
        } else {
            treeeSpawnerTool.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.NO_TREEE_SPAWNER_TOOL)));
        }
        textInv.setToolOrWeapon(19, new ItemStack(Material.DIAMOND_AXE, 1),
                Config.TREEE_SPAWNER_TOOL_CLICK ? !target.hasPermission("command.urextras.portal.treeespawnertool") ? Lang.colorize(Lang.NO_TREEE_SPAWNER_TOOL) : Lang.TREEE_SPAWNER_TOOL : Lang.NO_TREEE_SPAWNER_TOOL
                , treeeSpawnerTool);


        /* INFO: Open custom inventory */
        textInv.openInventory(target);
        /** ==== EFFECTS INVENTORY END ==== **/
        return true;
    }
}
