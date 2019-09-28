package net.pl3x.bukkit.urextras.listener;

import com.destroystokyo.paper.block.TargetBlockInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Config;
import net.pl3x.bukkit.urextras.util.inventories.CustomInventories;
import net.pl3x.bukkit.urextras.util.Particles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

/*
 * TODO:
 *   - Add quitEvent check and remove item from inventory
 */

/**
 * Listens for player clicks inside TreeeList Portal
 */
public class TreeeListPortalClickListener implements Listener {
    private UrExtras plugin;
    private BukkitTask taskToCancel;
    private boolean isRunning;
    private boolean hasTreeGenerated;
    private Player target;

    public TreeeListPortalClickListener(UrExtras plugin) {
        this.plugin = plugin;
    }

    /**
     * Validate block clicked
     * <p>
     * Verifies that the Tool is the 'Treee Spawner Tool'. Once the Tool is
     * verified, it will then be placed inside the players inventory so that
     * they may select a block to place the selected option.
     * <p>
     * Once a block is clicked, event will Check whether or not the proper
     * block was clicked. If the the block can not spawn a tree type,
     * event will cancel. If correct block was clicked a custom inventory
     * (Barrel) will be created with all available tree types
     *
     * @param clickEvent Player block click
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreeeBlockSlected(PlayerInteractEvent clickEvent){
        // INFO: Check for Diamond Axe
        if (clickEvent.getPlayer().getInventory().getItemInMainHand().getType() != Material.DIAMOND_AXE){
            Logger.debug("onTreeeBlockSelect | No Diamond Axe is hand, clickEvent cancelled");
            return;
        }

        // INFO: Check for a identifier (Custom Model Data)
        if (!clickEvent.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
            Logger.debug("onTreeeBlockSelect | Item does not have custom model data, clickEvent cancelled");
            return;
        }

        // INFO: Get the Identified (Custom Model Data)
        Integer itemInHandCustomModelData = clickEvent.getPlayer().getInventory().getItemInMainHand().getItemMeta().getCustomModelData();

        // INFO: Check for correct Custom Model Data
        if (!itemInHandCustomModelData.equals((int) 069001F)){
            Logger.debug("onTreeeBlockSelect | Item in hand does not equal to Tree Tool Custom Data");
            Logger.debug("onTreeeBlockSelect | Diamond Axe is has no lore which is in main hand, clickEvent cancelled");
            return;
        }

        // INFO: Cancel Player Interact Event
        clickEvent.setCancelled(true);

        target = clickEvent.getPlayer();
        ItemStack itemInHand = target.getInventory().getItemInMainHand();

        if (!Config.TREEE_SPAWNER_TOOL_CLICK){
            Logger.debug("onTreeeBlockSelect | " + target.getDisplayName() + " tried to use the " + itemInHand.getItemMeta().getDisplayName() + " but it is currently disabled.");
            Lang.send(target, "&7Sorry, the " + itemInHand.getItemMeta().getDisplayName() + " &7is currently disabled");
            return;
        }

        // INFO: Check for null
        if (clickEvent == null){
            Logger.debug("onTreeeBlockSelect | click event is null");
            return;
        }

        // INFO: Only spawn on blocks
        if (!clickEvent.getClickedBlock().getType().isBlock()){
            Logger.debug("onTreeeBlockSelect | clicked block is not a block");
            return;
        }

        Material clickedBlock = clickEvent.getClickedBlock().getType();

        if (clickedBlock == null){
            Logger.debug("onTreeeBlockSelect | Clicked block is null");
            return;
        }

        /*
        * INFO: Approve only certain blocks to spawn on
        *
        * TODO: Make clicked block configurable
        * */

        if (clickedBlock.equals(Material.DIRT)
                || clickedBlock.equals(Material.GRASS_BLOCK)
                || clickedBlock.equals(Material.GRASS)
                || clickedBlock.equals(Material.COARSE_DIRT)
                || clickedBlock.equals(Material.GRASS_PATH) /* ERROR: Does not spawn Acacia Tree */
                || clickedBlock.equals(Material.PODZOL)
                || clickedBlock.equals(Material.FARMLAND) /* ERROR: Does not spawn Acacia Tree */
                || clickedBlock.equals(Material.END_STONE) /* INFO: This check is for Chorus Plant */
            ){

            /*
             * INFO: Acacia Tree
             *
             * TODO:
             *   - Make tree meta oop
             *   - Add permissions for each tree type
             *   - Make Lang variable for each tree type
             */

            String[] treeTypeList = {"Acacia", "Birch", "RedWoodSpruce", "Jungle", "Oak", "DarkOak", "JungleSmall", "BirchTall", "CoCoa", "ChorusPlant", "Swamp", "BigOak", "JungleBush"};
            setTreeType(treeTypeList, target);

            /* TODO: Add next tree
             *  - Tall Redwood: Just a few leaves at the top
             *  - Red Mushroom: Big Red Mushroom; Short and fat
             *  - Brown Mushroom: Big brown mushroom; tall and unbrella-like
             *  - Mega Redwood: Mega redwood tree; 4 blocks wide and tall
             */

            Logger.debug("onTreeeBlockSelect | " + target.getDisplayName() + " clicked a applicable block with a " + itemInHand.getItemMeta().getDisplayName());

            //target.openInventory(treeListInventory);
            return;
        }

        Logger.debug("onTreeeBlockSelect | clickEvent was cancelled, you cannot spawn a tree there");
        Lang.send(target, Lang.colorize(Lang.CANNOT_SPAWN_TREEE_HERE.replace("{getToolName}", target.getInventory().getItemInMainHand().getItemMeta().getDisplayName() )) );
        return;
    }

    private void setTreeType(String[] treeTypeName, Player player){
        ItemStack treeType;
        String treeTypeTitle;
        List<String> treeTypeLore;
        int invSlot;


        // INFO: Create Treee List Inventory
        CustomInventories treeListInventory = new CustomInventories(Lang.TREEE_LIST_INVENTORY_TITLE, 18, event -> {
            // NOTICE: Do nothing
        }, plugin);


            for (String gettingTreeType : treeTypeName) {
                switch (gettingTreeType) {
                    case "Acacia":
                        invSlot = 0;
                        treeType = new ItemStack(Material.ACACIA_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_ACACIA ? Lang.TREEE_SPAWNED_ACACIA : Lang.TREEE_SPAWNED_ACACIANO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_ACACIA) {
                            if (Lang.TREEE_SPAWNED_LORE_ACACIA.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_ACACIA.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_ACACIA));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_ACACIANO)));
                        }
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "Birch":
                        invSlot = 1;
                        treeType = new ItemStack(Material.BIRCH_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_BIRCH ? Lang.TREEE_SPAWNED_BIRCH : Lang.TREEE_SPAWNED_BIRCHNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_BIRCH) {
                            if (Lang.TREEE_SPAWNED_LORE_BIRCH.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_BIRCH.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_BIRCH));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_BIRCHNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "RedWoodSpruce":
                        invSlot = 2;
                        treeType = new ItemStack(Material.SPRUCE_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_SPRUCE ? Lang.TREEE_SPAWNED_SPRUCE : Lang.TREEE_SPAWNED_SPRUCENO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_SPRUCE) {
                            if (Lang.TREEE_SPAWNED_LORE_SPRUCE.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_SPRUCE.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_SPRUCE));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_SPRUCENO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "Jungle":
                        invSlot = 3;
                        treeType = new ItemStack(Material.JUNGLE_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_JUNGLE ? Lang.TREEE_SPAWNED_JUNGLE : Lang.TREEE_SPAWNED_JUNGLENO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_JUNGLE) {
                            if (Lang.TREEE_SPAWNED_LORE_JUNGLE.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_JUNGLE.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_JUNGLE));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_JUNGLENO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "Oak":
                        invSlot = 4;
                        treeType = new ItemStack(Material.OAK_LOG);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_OAK ? Lang.TREEE_SPAWNED_OAK : Lang.TREEE_SPAWNED_OAKNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_OAK) {
                            if (Lang.TREEE_SPAWNED_LORE_OAK.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_OAK.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_OAK));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_OAKNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "DarkOak":
                        invSlot = 5;
                        treeType = new ItemStack(Material.DARK_OAK_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_DARK_OAK ? Lang.TREEE_SPAWNED_DARK_OAK : Lang.TREEE_SPAWNED_DARK_OAKNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_DARK_OAK) {
                            if (Lang.TREEE_SPAWNED_LORE_DARK_OAK.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_DARK_OAK.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_DARK_OAK));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_DARK_OAKNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "JungleSmall":
                        invSlot = 6;
                        treeType = new ItemStack(Material.STRIPPED_JUNGLE_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_JUNGLE_SMALL ? Lang.TREEE_SPAWNED_JUNGLE_SMALL : Lang.TREEE_SPAWNED_JUNGLE_SMALLNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_JUNGLE_SMALL) {
                            if (Lang.TREEE_SPAWNED_LORE_JUNGLE_SMALL.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_JUNGLE_SMALL.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_JUNGLE_SMALL));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_JUNGLE_SMALLNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "BirchTall":
                        invSlot = 7;
                        treeType = new ItemStack(Material.STRIPPED_BIRCH_LOG);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_BIRCH_TALL ? Lang.TREEE_SPAWNED_BIRCH_TALL : Lang.TREEE_SPAWNED_BIRCH_TALLNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_BIRCH_TALL) {
                            if (Lang.TREEE_SPAWNED_LORE_BIRCH_TALL.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_BIRCH_TALL.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_BIRCH_TALL));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_BIRCH_TALLNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "CoCoa":
                        invSlot = 8;
                        treeType = new ItemStack(Material.JUNGLE_WOOD, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_COCOA ? Lang.TREEE_SPAWNED_COCOA : Lang.TREEE_SPAWNED_COCOANO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_COCOA) {
                            if (Lang.TREEE_SPAWNED_LORE_COCOA.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_COCOA.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_COCOA));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_COCOANO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "ChorusPlant":
                        invSlot = 9;
                        treeType = new ItemStack(Material.CHORUS_FLOWER, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_CHORUS_PLANT ? Lang.TREEE_SPAWNED_CHORUS_PLANT : Lang.TREEE_SPAWNED_CHORUS_PLANTNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_CHORUS_PLANT) {
                            if (Lang.TREEE_SPAWNED_LORE_CHORUS_PLANT.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_CHORUS_PLANT.split(";");
                                for (int newlineLore = 0; newlineLore < newLine.length; ++newlineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newlineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_CHORUS_PLANT));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_CHORUS_PLANTNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "Swamp":
                        invSlot = 10;
                        treeType = new ItemStack(Material.VINE);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_SWAMP ? Lang.TREEE_SPAWNED_SWAMP : Lang.TREEE_SPAWNED_SWAMPNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_SWAMP) {
                            if (Lang.TREEE_SPAWNED_LORE_SWAMP.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_SWAMP.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_SWAMP));
                            }
                        } else {
                            treeTypeLore.add(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_SWAMPNO));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "BigOak":
                        invSlot = 11;
                        treeType = new ItemStack(Material.STRIPPED_OAK_LOG, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_BIG_OAK ? Lang.TREEE_SPAWNED_BIG_OAK : Lang.TREEE_SPAWNED_BIG_OAKNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_BIG_OAK) {
                            if (Lang.TREEE_SPAWNED_LORE_BIG_OAK.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_BIG_OAK.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_BIG_OAK));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_BIG_OAKNO)));
                        }
                        //break;
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                    case "JungleBush":
                        invSlot = 12;
                        treeType = new ItemStack(Material.TALL_GRASS, 1);
                        treeTypeTitle = Lang.colorize(Config.TREEE_LIST_JUNGLE_BUSH ? Lang.TREEE_SPAWNED_JUNGLE_BUSH : Lang.TREEE_SPAWNED_JUNGLE_BUSHNO);
                        treeTypeLore = new ArrayList<>();
                        if (Config.TREEE_LIST_JUNGLE_BUSH) {
                            if (Lang.TREEE_SPAWNED_LORE_JUNGLE_BUSH.contains(";")) {
                                String[] newLine = Lang.TREEE_SPAWNED_LORE_JUNGLE_BUSH.split(";");
                                for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore) {
                                    treeTypeLore.add(Lang.colorize(newLine[newLineLore]));
                                }
                            } else {
                                treeTypeLore.add(Lang.colorize(Lang.TREEE_SPAWNED_LORE_JUNGLE_BUSH));
                            }
                        } else {
                            treeTypeLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.TREEE_SPAWNED_JUNGLE_BUSHNO)));
                        }
                        treeListInventory.setToolOrWeapon(invSlot, treeType, treeTypeTitle, treeTypeLore);
                        break;
                    default:
                        break;
                }

            }
            treeListInventory.openInventory(player);
    }

    /**
     * Treee List Portal (custom inventory)
     * <p>
     * Checks what was clicked inside the Treee List Portal Inventory.
     * <p>
     * Once a player clicks on the tree type they would like to spawn,
     * custom inventory will close and the custom tool will be removed
     * from players inventory.
     *
     * @param inventoryClickEvent get clicked inventory.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreeeCreate(InventoryClickEvent inventoryClickEvent){
        String inventoryName = inventoryClickEvent.getWhoClicked().getOpenInventory().getTitle();
        if (inventoryName != Lang.TREEE_LIST_INVENTORY_TITLE){
            Logger.debug("onTreeeCreate | This inventory is not Tree List, return.");
            return;
        }

        Player target = (Player) inventoryClickEvent.getWhoClicked();
        ItemStack cursor = inventoryClickEvent.getCursor(); // Item/Block Placed
        ItemStack clicked = inventoryClickEvent.getCurrentItem();

        // NOTICE: Stopping all clickable events
        if (inventoryName.startsWith(Lang.TREEE_LIST_INVENTORY_TITLE)){
            inventoryClickEvent.setCancelled(true);
            Logger.debug("onTreeeCreate | Cancelling inventory click for Treee List Portal");
        }

        /* NOTICE: NULL CHECK START **/
        if (cursor == null){
            Logger.debug("onTreeeCreate | Cursor went null onTreeeCreate");
            inventoryClickEvent.setCancelled(true);
            return;
        }

        if (cursor.getType() == null){
            Logger.debug("onTreeeCreate | Cursor getType() went null onTreeeCreate");
            inventoryClickEvent.setCancelled(true);
            return;
        }

        if(clicked == null){
            Logger.debug("onTreeeCreate | Clicked went null onTreeeCreate");
            inventoryClickEvent.setCancelled(true);
            return;
        }

        if(target == null){
            Logger.debug("onTreeeCreate | Target went null onTreeeCreate");
            inventoryClickEvent.setCancelled(true);
            return;
        }
        /* NOTICE: NULL CHECK END **/

        /*
         * NOTICE: Check if player click option
         * TODO:
         *   - Make slot configure
         */
        if (clicked.getType() == Material.ACACIA_LOG
                && inventoryClickEvent.getSlot() == 0
                || clicked.getType() == Material.BIRCH_LOG
                && inventoryClickEvent.getSlot() == 1
                || clicked.getType() == Material.SPRUCE_LOG
                && inventoryClickEvent.getSlot() == 2
                || clicked.getType() == Material.JUNGLE_LOG
                && inventoryClickEvent.getSlot() == 3
                || clicked.getType() == Material.OAK_LOG
                && inventoryClickEvent.getSlot() == 4
                || clicked.getType() == Material.DARK_OAK_LOG
                && inventoryClickEvent.getSlot() == 5
                || clicked.getType() == Material.STRIPPED_JUNGLE_LOG
                && inventoryClickEvent.getSlot() == 6
                || clicked.getType() == Material.STRIPPED_BIRCH_LOG
                && inventoryClickEvent.getSlot() == 7
                || clicked.getType() == Material.JUNGLE_WOOD
                && inventoryClickEvent.getSlot() == 8
                || clicked.getType() == Material.CHORUS_FLOWER
                && inventoryClickEvent.getSlot() == 9
                || clicked.getType() == Material.VINE
                && inventoryClickEvent.getSlot() == 10
                || clicked.getType() == Material.STRIPPED_OAK_LOG
                && inventoryClickEvent.getSlot() == 11
                || clicked.getType() == Material.TALL_GRASS
                && inventoryClickEvent.getSlot() == 12
                ) {
            if (!Config.TREEE_LIST_ACACIA
                    || !Config.TREEE_LIST_BIRCH
                    || !Config.TREEE_LIST_SPRUCE
                    || !Config.TREEE_LIST_JUNGLE
                    || !Config.TREEE_LIST_OAK
                    || !Config.TREEE_LIST_DARK_OAK
                    || !Config.TREEE_LIST_JUNGLE_SMALL
                    || !Config.TREEE_LIST_BIRCH_TALL
                    || !Config.TREEE_LIST_COCOA
                    || !Config.TREEE_LIST_CHORUS_PLANT
                    || !Config.TREEE_LIST_SWAMP
                    || !Config.TREEE_LIST_BIG_OAK
                    || !Config.TREEE_LIST_JUNGLE_BUSH
                    ){
                Logger.debug("onTreeeCreate | " + target.getDisplayName() + " tried to spawn a " + clicked.getItemMeta().getDisplayName() + " but could not since this tree type is disabled.");
                Lang.send(target,Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", clicked.getItemMeta().getDisplayName())));
                target.closeInventory();
                return;
            }
        }

        Logger.debug("onTreeeCreate | " + target.getDisplayName() + " clicked " + clicked.getItemMeta().getDisplayName() + ".");

        TargetBlockInfo blockInfo = target.getTargetBlockInfo(10);
        Location relativeBlock = blockInfo.getRelativeBlock().getLocation();

        /*
         * TODO:
         *   - Add oop check for clicked option
         *   - Chorus Plant does not spawn on grass, add check
         * */
        if (clicked.getType() == Material.ACACIA_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.ACACIA);
        } else if (clicked.getType() == Material.BIRCH_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.BIRCH);
        } else if (clicked.getType() == Material.SPRUCE_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.REDWOOD);
        } else if (clicked.getType() == Material.JUNGLE_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.JUNGLE);
        } else if (clicked.getType() == Material.OAK_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.TREE);
        } else if (clicked.getType() == Material.DARK_OAK_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.DARK_OAK);
        } else if (clicked.getType() == Material.STRIPPED_JUNGLE_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.SMALL_JUNGLE);
        } else if (clicked.getType() == Material.STRIPPED_BIRCH_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.TALL_BIRCH);
        } else if (clicked.getType() == Material.JUNGLE_WOOD) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.COCOA_TREE);
        } else if (clicked.getType() == Material.CHORUS_FLOWER) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.CHORUS_PLANT); // INFO: Chorus Plant can only spawn on End Stone
        } else if (clicked.getType() == Material.VINE) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.SWAMP);
        } else if (clicked.getType() == Material.STRIPPED_OAK_LOG) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.BIG_TREE);
        } else if (clicked.getType() == Material.TALL_GRASS) {
            hasTreeGenerated = target.getWorld().generateTree(relativeBlock, TreeType.JUNGLE_BUSH);
        } else {
            Logger.debug("onTreeeCreate | No Tree was clicked, returned.");
            return;
        }

/* ################################################################################################################################
 * ################################################################################################################################
 * ################################################################################################################################
 * ################################################################################################################################
 * ######################################### ADDING OPTION CONFIG ENDS HERE########################################################
 * ################################################################################################################################
 * ################################################################################################################################
 * ################################################################################################################################
 * ################################################################################################################################
 */

        target.closeInventory();

        if (!hasTreeGenerated){
            Logger.debug("onTreeeCreate | " + target.getDisplayName() + " tried to spawn a " + clicked.getItemMeta().getDisplayName() + " but could not spawn on the block/item they clicked.");
            Lang.send(target, "&7Sorry, you cannot spawn a " + clicked.getItemMeta().getDisplayName() + " &7here. Please try again.");
            return;
        }

        /*
         * INFO: Adds an extra particle effect when the tree is spawned
         *
         * TODO:
         *   - Make apply to all selection, instead of applying it to all selections
         */
        TREEE_SPAWNED_PARTICLE:
        for (int particleSpawnTimer = 0; particleSpawnTimer < 4 ; particleSpawnTimer++) {
            for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                double radius = Math.sin(i);
                double y = Math.cos(i);
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 2) {
                    double x = Math.cos(a) * radius;
                    double z = Math.sin(a) * radius;
                    Location playerLoc = target.getLocation().add(x, y, z);
                    target.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, playerLoc, 1);
                    target.getLocation().subtract(x, y, z);
                    if (particleSpawnTimer >= 4) {
                        Logger.debug("onTreeeCreate | Tree Spawned Particle 'ForLoop' removed");
                        break TREEE_SPAWNED_PARTICLE;
                    }
                }
            }
        }

        /* NOTICE: Remove Treee Spawner Tool from inventory */
        target.getInventory().getItemInMainHand().setAmount(target.getInventory().getItemInMainHand().getAmount() - 1);

        /* NOTICE: Remove particle */
        if (hasTreeGenerated == true){
            isRunning = false;
        } else {
            isRunning = true;
        }

        if (!target.isOnline() || !isRunning){
            Particles taskRunning = UrExtrasPortalClickListener.particlesHashMap.get(target.getUniqueId());

            if (taskRunning != null && !taskRunning.isCancelled()) {
                taskRunning.cancel();
            }
            Logger.debug("onTreeeCreate | Removed Treee Spawner Particle.");
            isRunning = false;
            if (taskToCancel == null){
                Logger.debug("onTreeeCreate | No particles were spawned for the tool, cancel removing particles");
                return;
            }
            taskToCancel.cancel();
        }

        Logger.debug("onTreeeCreate | Target clicked and spawned a " + clicked.getItemMeta().getDisplayName() + ".");
        Lang.send(target,Lang.TREEE_SPAWNED_PLAYERMSG.replace("{getSpawnedName}", clicked.getItemMeta().getDisplayName()));

        // TODO: Add cooldown here

        return; // INFO: This, Removes particle for any selection
    }

    /**
     * If player quits server with custom Treee Spawn Tool inside their
     * inventory, this event will remove the custom tool to avoid expliots
     *
     * @param playerQuitEventToolInHand Remove custom tool
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuitToolInHand(PlayerQuitEvent playerQuitEventToolInHand){
        Player target = playerQuitEventToolInHand.getPlayer();
        ItemStack itemInHand = target.getInventory().getItemInMainHand();

        if(itemInHand.getType().isEmpty()){
            Logger.debug("onPlayerQuitToolInHand | Item in hand is null, return.");
            return;
        }

        /* NOTICE: Check for a identifier (Custom Model Data) */
        if (!target.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
            Logger.debug("onPlayerQuitToolInHand | The item in players hand, a" + target.getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase() + ", has no 'Custom Model Data', return.");
            return;
        }

        /* NOTICE: Get the Identified (Custom Model Data) */
        Integer itemInHandCustomModelData = target.getInventory().getItemInMainHand().getItemMeta().getCustomModelData();

        /* NOTICE: NPE check */
        if (itemInHandCustomModelData == null){
            return;
        }

        /* NOTICE: Check for correct Custom Model Data */
        if (target.getInventory().getItemInMainHand().getType() != Material.DIAMOND_AXE && !itemInHandCustomModelData.equals((int) 069001F)){
            Logger.debug("onPlayerQuitToolInHand | Item in hand does not equal to Tool Custom Data");
            return;
        }

        /* NOTICE: Remove Treee Spawner Tool from inventory */
        target.getInventory().getItemInMainHand().setAmount(target.getInventory().getItemInMainHand().getAmount() - 1);

        Logger.debug("onPlayerQuitToolInHand | Player Tool was removed since the player quit server");
        return;
    }


    /**
     * If a player is kicked from the server, let's make sure their custom tool is removed.
     *
     * @param playerKickEventToolInHand Remove custom tool
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKickToolInHand(PlayerKickEvent playerKickEventToolInHand){
        /*
         * ERROR:
         *   - When player leaves server and rejoin with item, the taskToCancel BukkitTask
         *     give NPE because effect does not reapply
         *
         * TODO:
         *   - Remove Tool if player is kicked from server
         */
        Player target = playerKickEventToolInHand.getPlayer();
        ItemStack itemInHand = target.getInventory().getItemInMainHand();

        if(itemInHand.getType().isEmpty()){
            Logger.debug("onPlayerKickToolInHand | Item in hand is null, return.");
            return;
        }

        /* NOTICE: Check for a identifier (Custom Model Data) */
        if (!target.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
            Logger.debug("onPlayerKickToolInHand | The item in players hand, a" + target.getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase() + ", has no 'Custom Model Data', return.");
            return;
        }

        /* NOTICE: Get the Identified (Custom Model Data) */
        Integer itemInHandCustomModelData = target.getInventory().getItemInMainHand().getItemMeta().getCustomModelData();

        /* NOTICE: NPE check */
        if (itemInHandCustomModelData == null){
            return;
        }

        /* NOTICE: Check for correct Custom Model Data */
        if (target.getInventory().getItemInMainHand().getType() != Material.DIAMOND_AXE && !itemInHandCustomModelData.equals((int) 069001F)){
            Logger.debug("onPlayerKickToolInHand | Item in hand does not equal to Tool Custom Data");
            return;
        }

        /* NOTICE: Remove Treee Spawner Tool from inventory */
        target.getInventory().getItemInMainHand().setAmount(target.getInventory().getItemInMainHand().getAmount() - 1);

        Logger.debug("onPlayerKickToolInHand | Player Tool was removed since the player was kicked from server");
        return;
    }


    /**
     * Make sure players custom tool is remove if their inventory is changed from survival.
     *
     * @param playerGameModeChangeEventToolInHand Remove custom tool
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerGameModeChangeToolInHand(PlayerGameModeChangeEvent playerGameModeChangeEventToolInHand){
        /*
         * TODO:
         *   - Remove Tool if players gamemode is changed from survival
         */
    }

    /**
     * Stops players from dropping the Treee Spawner Tool from their inventory
     *
     * @param playerDropItemEvent Stop Treee Spawner Tool for dropping out of hand
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent playerDropItemEvent){
        ItemStack droppedItem = playerDropItemEvent.getItemDrop().getItemStack();

        if (droppedItem == null){
            Logger.debug( "onPlayerDropItem | dropped item is null");
            return;
        }

        if (!droppedItem.hasItemMeta()) {
            Logger.debug("onPlayerDropItem | dropped item does not have itemmeta.");
            return;
        }

        if (!droppedItem.getItemMeta().hasCustomModelData()){
            Logger.debug("onPlayerDropItem | Dropped Item does not have custom model data");
            return;
        }

        Float droppedItemCustomModeldata = Float.valueOf(droppedItem.getItemMeta().getCustomModelData());

        if (!droppedItemCustomModeldata.equals(069001F)){
            Logger.debug("onPlayerDropItem | Dropped Item does not equal to Treee Spawner Tool Custom Model Data.");
            return;
        }

        playerDropItemEvent.setCancelled(true);
        Logger.debug("onPlayerDropItem | Player attempted to drop their Treee Spawner Tool.");

        Player target = playerDropItemEvent.getPlayer();
        Lang.send(target, Lang.colorize(Lang.TOOL_DROP_ATTEMPT)
                    .replace("{getToolName}", droppedItem.getItemMeta().getDisplayName()));
        return;

    }

    /**
     * Stops player from moving the Treee Spawner Tool around inside their inventory
     *
     * @param inventoryClickEvent Cancel event when player tries to move Tool
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInventoryClickWithToolInHand(InventoryClickEvent inventoryClickEvent){
        InventoryType inventoryType = inventoryClickEvent.getInventory().getType();

        if (!inventoryType.equals(InventoryType.CRAFTING)){
            Logger.debug("onPlayerInventoryClickWithToolInHand | Clicked inventory is not a players inventory.");
            return;
        }

        if (inventoryClickEvent.getCurrentItem() == null){
            Logger.debug("onPlayerInventoryClickWithToolInHand | Player did not click an item, cancel event");
            return;
        }

        ItemStack clickedItem = inventoryClickEvent.getCurrentItem();

        if (!clickedItem.hasItemMeta()){
            Logger.debug("onPlayerInventoryClickWithToolInHand | Clicked item does not have itemMeta, event cancelled.");
            return;
        }

        if (!clickedItem.getItemMeta().hasCustomModelData()){
            Logger.debug("onPlayerInventoryClickWithToolInHand | Clicked item does not have custom model data");
            return;
        }

        Float clickedItemModelData = Float.valueOf(clickedItem.getItemMeta().getCustomModelData());

        if (!clickedItemModelData.equals(069001F)){
            Logger.debug("onPlayerInventoryClickWithToolInHand | Clicked Item does not equal to Treee Spawner Tool Custom Model Data.");
            return;
        }

        inventoryClickEvent.setCancelled(true);
        Logger.debug("onPlayerInventoryClickWithToolInHand | Player tried to move the Treee Spawner Tool, Event cancelled.");
        Player target = (Player) inventoryClickEvent.getWhoClicked();
        Lang.send(target, Lang.TOOL_MOVE_ATTEMPT
                .replace("{getToolName}", clickedItem.getItemMeta().getDisplayName()));
        return;
    }

    /**
     * Removes Tree spawner tool when player dies
     *
     * @param playerDeathEvent Remove tool on death
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent){
        Iterator<ItemStack> iterator = playerDeathEvent.getDrops().iterator();
        Player target = playerDeathEvent.getEntity();

        while (iterator.hasNext()){
            if (getCustomModelData(iterator.next()).equals((int) 069001F)){
                Lang.send(target, Lang.colorize(Lang.ON_PLAYER_DEATH
                        .replace("{getToolName}", target.getInventory().getItemInMainHand().getItemMeta().getDisplayName())));
                iterator.remove();
                Logger.debug("onPlayerDeath | Player died, removed treee spawner tool");
            }
        }

        Particles taskRunning = UrExtrasPortalClickListener.particlesHashMap.get(target.getUniqueId());

        if (taskRunning != null && !taskRunning.isCancelled()) {
            taskRunning.cancel();
        }
        Logger.debug("onTreeeCreate | Removed Treee Spawner Particle.");

        isRunning = false;

        if (taskToCancel == null){
            Logger.debug("onTreeeCreate | No particles were spawned for the tool, cancel removing particles");
            return;
        }
        taskToCancel.cancel();

        return;
    }

    /**
     * Gets the ItemStack Custom Model Data
     *
     * @param itemStack Gets ItemStack
     * @return ItemStack Custom Model Data
     */
    public Integer getCustomModelData(ItemStack itemStack){
        Integer itemModelData = null;

        if (itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasCustomModelData()){
                Integer itemCustomModelData = itemMeta.getCustomModelData();
                itemModelData = itemCustomModelData;
            }
        }

        if (itemModelData == null){
            itemModelData = 0;
        }

        return itemModelData;
    }
}
