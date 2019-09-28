package net.pl3x.bukkit.urextras.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.configuration.Config;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.UrExtras;
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

/**
 * Listens to clicks inside UrExtras Portal
 */

public class UrExtrasPortalClickListener implements Listener {
    private UrExtras plugin;
    public static Map<UUID, Particles> particlesHashMap = new HashMap<>();
    private Particles treeeSpawnerTask;
    private Player target;

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
    public void onMainPortalClick(InventoryClickEvent inventoryClickEvent) {
        //String inventoryName = customInventoryClick.getInventoryName(); <-- ERROR: Gives NPE
        String inventoryName = inventoryClickEvent.getWhoClicked().getOpenInventory().getTitle();

        if (inventoryName != Lang.UREXTRAS_PORTAL_INVENTORY_TITLE){
            Logger.debug("onMainPortalClick | Not custom inventory. Return.");
            return;
        }


        /* NOTICE: Stopping all clickable events */
        inventoryClickEvent.setCancelled(true);
        Logger.debug("onMainPortalClick | Click Event was cancelled for the UrExtras Portal Inventory");
        target = (Player) inventoryClickEvent.getWhoClicked();

        ItemStack clicked = inventoryClickEvent.getCurrentItem(); // INFO: Slot/Item/Block Clicked
        ItemStack cursor = inventoryClickEvent.getCursor(); // INFO: Item/Block Placed

        if (clicked == null){
            Logger.debug("onUrExtrasPortalInventoryClick | The clicked inventory slot is null(empty).");
            return;
        }

        if (clicked.getType() == Material.APPLE ) { // NOTICE: Check if player closed custom inventory (Icon: Apple)
            target.closeInventory();

            Logger.debug("onMainPortalClick | Player clicked Apple inside UrExtras Portal");
            Lang.send(target, Lang.UREXTRAS_PORTAL_INVENTORY_CLOSED.replace("{getInventoryName}", Lang.UREXTRAS_PORTAL_INVENTORY_TITLE));
            return;
        } else if (clicked.getType() == Material.DIAMOND_AXE // NOTICE: Check for Treee Spawner Tool (Diamond Axe) was selected inside portal
                && inventoryClickEvent.getSlot() == 19){

            // INFO: check if tool is enabled in config
            if (!Config.TREEE_SPAWNER_TOOL_CLICK){
                Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + "clicked Tree Spawner Tool, however it is disabled in configs.");
                target.closeInventory();
                Lang.send(target, "&7Sorry the " + clicked.getItemMeta().getDisplayName() + " &7is currently disabled.");
                return;
            }

            // INFO: debug Stuff
            Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + " clicked Diamond Axe" + (!target.hasPermission("command.urextras.portal.treeespawnertool") ? " but, does not have permission to use the " + clicked.getItemMeta().getDisplayName() : "") );
            if (Config.DEBUG_MODE){
                Lang.send(target, "&2[&eDEBUG&2] &rYou clicked on UrExtras " + clicked.getItemMeta().getDisplayName() + "&r.");
            }

            // INFO: Check for permission
            if (!target.hasPermission("command.urextras.portal.treeespawnertool")){
                Lang.send(target, Lang.colorize(Lang.COMMAND_NO_PERMISSION_PORTAL
                                    .replace("{getClicked}", clicked.getItemMeta().getDisplayName()) ));
                target.closeInventory();
                return;
            }

            // INFO: Check and make sure players hand is empty
            if (!target.getInventory().getItemInMainHand().getType().isEmpty()){
                Logger.debug("onUrExtrasPortalInventoryClick | " + target.getDisplayName() + " clicked " + clicked.getItemMeta().getDisplayName() + " while their main hand was not empty, event cancelled.");
                Lang.send(target, Lang.HAND_NOT_EMPTY
                            .replace("{getClicked}", clicked.getItemMeta().getDisplayName()));
                target.closeInventory();
                return;
            }

            // INFO: Create Treee Spawner Tool
            setTool("treeeSpawnerTool");

            Lang.send(target, Lang.colorize(Lang.GIVE_TOOL
                                .replace("{getToolName}", clicked.getItemMeta().getDisplayName()) ));
            // TODO:
            //  - ADD Cooldown
            //  - ADD Check for cooldown
        } else {
            Logger.debug("onMainPortalClick | Nothing happened, returned.");
        }

        // INFO: Add tool particles
        treeeSpawnerTask = new Particles(target);
        treeeSpawnerTask.runTaskTimer(plugin, 0L, 2L);
        particlesHashMap.put(target.getUniqueId(), treeeSpawnerTask);

        target.closeInventory();
        return;
    }


    /**
     * Set custom tool/weapon in players main hand
     *
     * @param toolWeaponName Tool/Weapon string name
     */
    private void setTool(String toolWeaponName) {
        ItemStack tool = null;

        switch (toolWeaponName){
            case "treeeSpawnerTool":
                tool = new ItemStack(Material.DIAMOND_AXE, 1);
                ItemMeta toolItemMeta = tool.getItemMeta();
                toolItemMeta.setDisplayName(Lang.colorize(Lang.TREEE_SPAWNER_TOOL));
                toolItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                toolItemMeta.setCustomModelData((int) 069001F); // TODO: Make int config option
                List<String> toolLore = new ArrayList<>();
                toolLore.add("&Click location where you would");
                toolLore.add("&7like to spawn your &6Tree Type&7.");
                toolItemMeta.setLore(toolLore);
                tool.setItemMeta(toolItemMeta);
                break;
            default:
                break;
        }

        if (!tool.equals(null)){
            target.getInventory().setItemInMainHand(tool);
        }
    }
}