package net.pl3x.bukkit.urextras.util.inventories;

import java.util.List;
import net.pl3x.bukkit.urextras.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CustomInventories implements Listener {
    private int inventorySlots;
    private String inventoryName;
    private OptionClickEventHandler handler;
    private Plugin plugin;
    private Player player;
    private String[] itemStackName;
    private ItemStack[] itemStacks;

    /**
     * create custom inventory
     *
     * @param inventoryName Set inventory name
     * @param inventorySlots set inventory slot amount
     * @param handler Custom inventory event
     * @param plugin Register listener
     */
    public CustomInventories (String inventoryName, int inventorySlots, OptionClickEventHandler handler, Plugin plugin){
        this.inventoryName = inventoryName;
        this.inventorySlots = inventorySlots;
        this.handler = handler;
        this.plugin = plugin;
        this.itemStackName = new String[inventorySlots];
        this.itemStacks = new ItemStack[inventorySlots];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Set the tool/weapon inside custom inventory
     *
     * @param itemStackSlot Set slot position
     * @param itemStack Set item
     * @param itemStackNames Set item name
     * @param itemStackLore Set item lore
     * @return Item with custom data
     */
    public CustomInventories setToolOrWeapon(int itemStackSlot, ItemStack itemStack, String itemStackNames, List<String> itemStackLore){
        itemStackName[itemStackSlot] = itemStackNames;
        itemStacks[itemStackSlot] = setItem(itemStack, itemStackNames, itemStackLore);
        return this;
    }

    /**
     * Allows you to set to a specific player
     *
     * @param player Set to specific player
     */
    public void setPlayer(Player player){
        this.player = player;
    }

    /**
     * Check if the player is specific
     *
     * @return Specific player
     */
    public boolean isPlayer(){
        return player != null;
    }

    /**
     * Set the custom item inside the custom inventory
     *
     * @param itemStack Set the item
     * @param itemName Set the item name
     * @param itemLore Set the item lore
     *
     * @return all data from item
     */
    public ItemStack setItem(ItemStack itemStack, String itemName, List<String> itemLore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Set the custom item inside the custom inventory
     *
     * @param itemStack Set the item
     * @param itemName Set the item name
     * @param modelData set a custom Model Data
     * @param itemLore Set the item lore
     * @return all data from item
     */
    public ItemStack createItem(ItemStack itemStack, String itemName, int modelData, List<String> itemLore){
        ItemStack itemStackCreated = itemStack;
        ItemMeta itemMeta = itemStackCreated.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setCustomModelData(modelData);
        itemMeta.setLore(itemLore);
        itemStackCreated.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Gets custom inventory
     *
     * @param player Gets player looking at custom inventory
     * @return Get custom inventory
     */
    private Inventory getInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, inventorySlots, inventoryName);
        for (int i = 0; i < itemStacks.length; i++){
            if (itemStacks[i] != null){
                inventory.setItem(i, itemStacks[i]);
            }
        }
        return inventory;
    }

    /**
     * Open custom inventory
     *
     * @param player Get player that is opening custom inventory
     */
    public void openInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, inventorySlots, inventoryName);
        for (int i = 0; i < itemStacks.length; i++){
            if (itemStacks[i] != null){
                inventory.setItem(i, itemStacks[i]);
            }
        }
        player.openInventory(inventory);
    }

    /**
     * Closes the custom inventory player is looking at
     *
     * @param player Gets player looking at custom inventory
     * @return close custom inventory
     */
    public CustomInventories close(Player player){
        if (player.getOpenInventory().getTitle().equals(inventoryName)){
            player.closeInventory();
        }
        return this;
    }

    public void destroy(){
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        itemStackName = null;
        itemStacks = null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent inventoryClickEvent){
        if (inventoryClickEvent.getInventory().getType().name().equals(inventoryName)){
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getClick() != ClickType.LEFT){
                Logger.debug("onInventoryClick | Not Left click, returned");
                return;
            }
            int invSlot = inventoryClickEvent.getRawSlot();
            if (invSlot >= 0 && invSlot < inventorySlots && itemStackName[invSlot] != null){
                Plugin plugin = this.plugin;
                OptionClickEvent event = new OptionClickEvent( (Player) inventoryClickEvent.getWhoClicked(), invSlot, itemStackName[invSlot]);
                handler.onOptionClick(event);
                ((Player) inventoryClickEvent.getWhoClicked()).updateInventory();
                if (event.isInventoryClose()){
                    final Player player = (Player) inventoryClickEvent.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.closeInventory());
                }
                if (event.isDestroy()){
                    destroy();
                }
            }
        }
    }

    /**
     * Custom Event Handler Interface
     */
    public interface OptionClickEventHandler{
        public void onOptionClick(OptionClickEvent event);
    }

    /**
     * Custom Event
     */
    public class OptionClickEvent {
        private Player player;
        private int inventorySlot;
        private String inventoryName;
        private boolean inventoryClose;
        private boolean destroy;
        //private ItemStack itemStack; // Maybe not needed

        /**
         * Event set
         *
         * @param player Set Player
         * @param inventorySlot Set Inventory Slot
         * @param inventoryName Set Inventory Name
         * //@param itemStack Set ItemStack
         */
        public OptionClickEvent(Player player, int inventorySlot, String inventoryName){
            this.player = player;
            this.inventorySlot = inventorySlot;
            this.inventoryName = inventoryName;
            this.inventoryClose = true;
            this.destroy = false;
            //this.itemStack = itemStack;
        }

        /**
         * Get Player
         *
         * @return Player
         */
        public Player getPlayer(){
            return player;
        }

        /**
         * get Inventory Slot
         *
         * @return Inventory Slot
         */
        public int getInventorySlot(){
            return inventorySlot;
        }

        /**
         * Get Inventory Name
         *
         * @return Inventory Name
         */
        public String getInventoryName(){
            return inventoryName;
        }

        /*
         * Get Clicked ItemStack.
         *
         * @return Clicked ItemStack
         *
        public ItemStack getItemStack(){
            return this.itemStack;
        }
        */

        /**
         * Check if Inventory is Closed
         *
         * @return Inventory Closed
         */
        public boolean isInventoryClose(){
            return inventoryClose;
        }

        /**
         * Check if Destroyed
         *
         * @return Destroyed
         */
        public boolean isDestroy(){
            return destroy;
        }

        /**
         * Set Inventory Close. This will exit out of the custom inventory the player is looking at.
         *
         * @param inventoryClose Set Inventory Close
         */
        public void setInventoryClose(boolean inventoryClose){
            this.inventoryClose = inventoryClose;
        }

        /**
         * This will kill allow you to redefine the custom inventory and not duplicate the instance,
         * which would result in the event being fired twice or more.e or more.
         *
         * @param destroy Kills the custom inventory
         */
        public void setDestroy(boolean destroy){
            this.destroy = destroy;
        }
    }
}
