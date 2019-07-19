package net.pl3x.bukkit.urextras.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Config;
import net.pl3x.bukkit.urextras.util.Particles;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 *
 * TODO:
 *   - Add cooldowns to each feature
 *
 * INFO:
 *   | Check inventory Name
 *   | Check Item has meta
 *   | Check item type is <ItemType>
 *   | Check current item type byte is ##
 *   |
 *   | if (sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("CmdCD")) {
 *   |          net.pl3x.bukkit.cmdcd.CmdCD.addCooldown(command, ((Player) sender).getUniqueId(), Config.ME_COOLDOWN);
 *   | }
 *   |
 *   | inventoryClickEvent.getRawSlot() > inventoryClickEvent.getInventory().getSize()
 *   | Tells you when the players inventory was clicked
 *   | Applying < will tell you when a player clicks the custom number inventory
 */

/**
 * Listens to clicks inside UrExtras Portal
 */

public class UrExtrasPortalClickListener implements Listener {
    private UrExtras plugin;
    public static Map<UUID, Particles> particlesHashMap = new HashMap<>();
    public Particles treeeSpawnerTask;

    public UrExtrasPortalClickListener(UrExtras plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks what was clicked inside the UrExtras Portal Inventory (custom inventory)
     * <p>
     * Once a custom tool/weapon is clicked, the custom inventory will close with the
     * custom tool/weapon placed inside their inventory.
     * <p>
     * A custom particle effect will be added to each custom tool & weapon which will
     * be removed after the weapon/tool is finished being used.
     *
     * @param inventoryClickEvent Get clicked inventory
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onUrExtrasPortalInventoryClick(InventoryClickEvent inventoryClickEvent) {
        String inventoryName = inventoryClickEvent.getWhoClicked().getOpenInventory().getTitle();
        if (inventoryName != Lang.UREXTRAS_PORTAL_INVENTORY_TITLE){
            Logger.debug("onUrExtrasPortalInventoryClick | This inventory is not UrExtras Portal, return.");
            return;
        }

        /* NOTICE: Stopping all clickable events */
        inventoryClickEvent.setCancelled(true);
        Logger.debug("onUrExtrasPortalInventoryClick | Click Event was cancelled for the UrExtras Portal Inventory");

        Player target = (Player) inventoryClickEvent.getWhoClicked();
        ItemStack cursor = inventoryClickEvent.getCursor(); // Item/Block Placed
        ItemStack clicked = inventoryClickEvent.getCurrentItem(); // Slot/Item/Block Clicked

        /*
         * NOTICE: NULL CHECK START
         */
        if (cursor == null){
            Logger.debug("onUrExtrasPortalInventoryClick | Cursor is equal to null");
            return;
        }

        if (cursor.getType() == null){
            Logger.debug("onUrExtrasPortalInventoryClick | Cursor getType() is equal to null");
            return;
        }

        if(clicked == null){
            Logger.debug("onUrExtrasPortalInventoryClick | The clicked inventory slot is null(empty).");
            return;
        }

        if(target == null){
            Logger.debug("onUrExtrasPortalInventoryClick | Target is equal to null");
            return;
        }
        /* NOTICE: NULL CHECK END */

        /*
         * NOTICE: Check if player closed custom inventory (Icon: Apple)
         */
        if (clicked.getType() == Material.APPLE
                && clicked.getItemMeta().getDisplayName().startsWith("Close", 2)) {
            target.closeInventory();

            Logger.debug("");
            Lang.send(target, Lang.UREXTRAS_PORTAL_INVENTORY_CLOSED.replace("{getInventoryName}", inventoryName));
            return;
        }

        /*
         * NOTICE: Check if the TREEE SPAWNER TOOL (Diamond Axe) was selected inside the UrExtras Portal
         */
        if (clicked.getType() == Material.DIAMOND_AXE
                && clicked.getItemMeta().getDisplayName().startsWith("Treee", 2)
                && inventoryClickEvent.getSlot() == 19 ){

            /* NOTICE: Check if the tool is disabled in configs */
            if (!Config.TREEE_SPAWNER_TOOL_CLICK){
                Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + " clicked Treee Spawner Tool, however it is disabled in configs");
                Lang.send(target, "&7Sorry the " + clicked.getItemMeta().getDisplayName() + " &7is currently disabled.");
                target.closeInventory();
                return;
            }

            Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + " clicked Diamond Axe" + (!target.hasPermission("command.urextras.portal.treeespawnertool") ? "but, does not have permission to use the " + clicked.getItemMeta().getDisplayName() : "" ) );

            /* NOTICE: Debug stuff */
            if (Config.DEBUG_MODE) {
                Lang.send(target, "&2[&eDEBUG&2]&r You clicked UrExtras Portal &bDiamond Axe&r.");
            }

            /* NOTICE: Check for permission node */
            if (!target.hasPermission("command.urextras.portal.treeespawnertool")){
                Lang.send(target, Lang.colorize(Lang.COMMAND_NO_PERMISSION_PORTAL
                        .replace("{getClicked}",  clicked.getItemMeta().getDisplayName() ) ));

                target.closeInventory();
                return;
            }

            /* NOTICE: Make sure that the player has nothing in main hand */
            if (!target.getInventory().getItemInMainHand().getType().isEmpty()) {
                Lang.send(target, Lang.HAND_NOT_EMPTY
                        .replace("{getClicked}", clicked.getItemMeta().getDisplayName()));

                Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + " clicked " + clicked.getItemMeta().getDisplayName() + " while their main hand was not empty, event cancelled.");

                target.closeInventory();
                return;
            }

            /* NOTICE: Create Treee Spawner Tool */
            ItemStack treeSpawnerTool = new ItemStack(Material.DIAMOND_AXE, 1);
            ItemMeta treeSpawnerToolMeta = treeSpawnerTool.getItemMeta();
            treeSpawnerToolMeta.setDisplayName(Lang.colorize(Lang.TREEE_SPAWNER_TOOL));
            treeSpawnerToolMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            treeSpawnerToolMeta.setCustomModelData((int) 069001F); // TODO: Add as config option
            ArrayList<String> treeSpawnerToolLore = new ArrayList<>();
            treeSpawnerToolLore.add(Lang.colorize("&7Click location where you would"));
            treeSpawnerToolLore.add(Lang.colorize("&7like to spawn your &6Tree Type&7."));
            treeSpawnerToolMeta.setLore(treeSpawnerToolLore);
            treeSpawnerTool.setItemMeta(treeSpawnerToolMeta);

            /* NOTICE: Set Treee Spawner Tool in targets main hand */
            target.getInventory().setItemInMainHand(treeSpawnerTool);
            Lang.send(target, Lang.colorize(Lang.GIVE_TOOL.replace("{getToolName}", treeSpawnerTool.getItemMeta().getDisplayName() )) );

            /*
             * TODO:
             *   - Add check for cooldown
             *   - Add cooldown
            */

            treeeSpawnerTask = new Particles(target);
            treeeSpawnerTask.runTaskTimer(plugin,0L,2L);
            particlesHashMap.put(target.getUniqueId(), treeeSpawnerTask);

            target.closeInventory();
            return;
        }

        /*
         * TODO: Make next feature for the UrExtras Portal
         */

        target.closeInventory();
        return;
    }
}

