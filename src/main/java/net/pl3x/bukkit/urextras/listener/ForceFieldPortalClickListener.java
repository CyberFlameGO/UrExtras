package net.pl3x.bukkit.urextras.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.pl3x.bukkit.cmdcd.CmdCD;
import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Config;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.util.CustomInventories;
import net.pl3x.bukkit.urextras.util.Particles;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ForceFieldPortalClickListener implements Listener {
    private static UrExtras plugin;
    private Player target;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Particles forceFieldParticleTask;
    private boolean taskRunning;
    private CmdCD cmdCD;

    public ForceFieldPortalClickListener(UrExtras plugin) {
        this.plugin = plugin;
    }

    /**
     * This event will fire will a player has the Force Field Weapon
     * in their inventory. Once activated, a Force Field Weapon Extra
     * inventory will appear
     *
     * @param playerInteractEvent Checks for player clicks
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onForceFieldWeaponClick(PlayerInteractEvent playerInteractEvent){
        System.out.println("PlayerInteractEvent: " + playerInteractEvent.getAction());

        // INFO: Check for Gray Dye
        if (playerInteractEvent.getPlayer().getInventory().getItemInMainHand().getType() != Material.GRAY_DYE){
            Logger.debug("onForceFieldWeaponClick | No Gray Dye in main hand, playerInteractEvent returned.");
            return;
        }

        // INFO: Check for identifier (Custom Model Data)
        if (!playerInteractEvent.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
            Logger.debug("onForceFieldWeaponClick | Item does not have customomdel data, player Interact Event returned.");
            return;
        }

        //  INFO: Get the identified (Custom Model Data)
        Integer itemInHandCustomModelData = playerInteractEvent.getPlayer().getInventory().getItemInMainHand().getItemMeta().getCustomModelData();

        // INFO: Check for correct Custom Model Data
        if (!itemInHandCustomModelData.equals((int) 069002F)){
            Logger.debug("onForceFieldWeaponClick | Item in hand does not equal to the Force Field Weapon Custom Model Data, returned");
            return;
        }

        // INFO: Cancel Player Interact Event
        playerInteractEvent.setCancelled(true);

        target = playerInteractEvent.getPlayer();
        ItemStack itemInHand = target.getInventory().getItemInMainHand();

        if (!Config.FORCE_FIELD_WEAPON_CLICK){
            Logger.debug("onForceFieldWeaponClick | " + target.getDisplayName() + " tried to use the " + itemInHand.getItemMeta().getDisplayName() + " but it is currently disabled.");
            Lang.send(target, "&7Sorry, the " + itemInHand.getItemMeta().getDisplayName() + " &7is currently disabled.");
            return;
        }

        // INFO: Check for null
        if (playerInteractEvent == null){
            Logger.debug("onForceFieldWeaponClick | Click event is null");
            return;
        }

        Logger.debug("onForceFieldWeaponClick | Target is not NULL");

        Action action = playerInteractEvent.getAction();

        // INFO: Check for Click Action
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL){

            Logger.debug("onForceFieldWeaponClick | Target create an click action.");

            // INFO: Create Force Field Extras
            String[]  forceFieldExtrasList = {"standard", "knockback"};
            //setForceFieldExtra(forceFieldExtrasList, target);
            setForceFieldWeaponExtraType(forceFieldExtrasList, target);

            Logger.debug("onForceFieldWeaponClick | " + target.getDisplayName() + " activated the Force Field Extras Portal.");

            target.getInventory().getItemInMainHand().setAmount(target.getInventory().getItemInMainHand().getAmount() - 1);
            return;
        }
    }

    /**
     * Checks for events when the Force Field inventory is in use
     *
     * @param inventoryClickEvent Inventory Click Event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onForceFieldExtraClick(InventoryClickEvent inventoryClickEvent) {
        String inventoryName = inventoryClickEvent.getWhoClicked().getOpenInventory().getTitle();

        if (inventoryName != Lang.FORCE_FIELD_EXTRA_INVENTORY_TITLE){
            Logger.debug("onForceFieldExtraClick | Not force field custom inventory, return.");
            return;
        }

        // INFO: Stop all clickable events
        inventoryClickEvent.setCancelled(true);
        Logger.debug("onForceFieldExtraClick | Click event was cancelled for Force Field Inventory.");

        Player target = (Player) inventoryClickEvent.getWhoClicked();

        ItemStack clicked = inventoryClickEvent.getCurrentItem(); // INFO: Slot/Item/Block Clicked
        ItemStack cursor = inventoryClickEvent.getCursor(); // INFO: Item/Block Placed

        // INFO: NULL CHECK START
        if (clicked == null){
            Logger.debug("onForceFieldExtraClick | Target clicked an empty inventory slot, return.");
            return;
        }

        if (cursor == null){
            Logger.debug("onForceFieldExtraClick | Cursor went null, return.");
            return;
        }

        if (cursor.getType() == null){
            Logger.debug("onForceFieldExtraClick | Cursor getType() went null, return.");
            return;
        }

        if(target == null){
            Logger.debug("onForceFieldExtraClick | Target went null, return.");
            return;
        }
        // INFO: NULL CHECK END

        Logger.debug("onForceFieldExtraClick | " + target.getDisplayName() + " clicked " + clicked.getItemMeta().getDisplayName() + ".");

        if (clicked.getType() == Material.GRAY_DYE){
            if (!Config.FORCE_FIELD_EXTRA_LIST_STANDARD){
                Logger.debug("onForceFieldExtraClick | " + target.getDisplayName() + " tried to click " + clicked.getItemMeta().getDisplayName() + " but could not since this Force Field Extra is disabled.");
                Lang.send(target, Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", clicked.getItemMeta().getDisplayName())));
                target.closeInventory();
                return;
            }

            setForceFieldExtra("standard", target);

            /*
             * Set Particle & Timer
             */
            forceFieldParticleTask = new Particles(target, Particle.valueOf(Config.PARTICLE_TYPE_FORCE_FIELD_STANDARD.toUpperCase()));
            final Runnable spawnParticle = () -> forceFieldParticleTask.run();
            final ScheduledFuture<?> particleHandle = scheduler.scheduleAtFixedRate(spawnParticle, 0, 100, TimeUnit.MILLISECONDS);
            scheduler.schedule(() -> {
                    particleHandle.cancel(true);
            }, Config.FORCE_FIELD_WEAPON_TIMER, TimeUnit.SECONDS);

            taskRunning = true;

            if (particleHandle.isCancelled()) {
                Logger.debug("onForceFieldExtraClick | Removed Force Field Particles.");
                Lang.send(target, Lang.colorize("&6Force Field &7timer has ran out, &6Force Field &7removed."));
                taskRunning = false;
            }

        } else if (clicked.getType() == Material.DIAMOND_SWORD) {

            if (!Config.FORCE_FIELD_EXTRA_LIST_KNOCKBACK){
                Logger.debug("onForceFieldExtraClick | " + target.getDisplayName() + " tried to click " + clicked.getItemMeta().getDisplayName() + " but could not since this Force Field Extra is disabled.");
                Lang.send(target, Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", clicked.getItemMeta().getDisplayName())));
                target.closeInventory();
                return;
            }

            setForceFieldExtra("knockback", target);

            forceFieldParticleTask = new Particles(target, Particle.valueOf(Config.PARTICLE_TYPE_FORCE_FIELD_KNOCKBACK.toUpperCase()));

            final Runnable spawnParticle = () -> {
                forceFieldParticleTask.run();

                if (target.getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALL)){
                    target.setHealth(
                            Math.min(target.getHealth() + 1, target.getMaxHealth())
                    );
                }

            };
            // TODO: Make player get no damage!!!
            final ScheduledFuture<?> particleHandler = scheduler.scheduleAtFixedRate(spawnParticle, 0, 100, TimeUnit.MILLISECONDS);

            scheduler.schedule(() -> {
                particleHandler.cancel(true);
            }, Config.FORCE_FIELD_WEAPON_TIMER, TimeUnit.SECONDS);

            taskRunning = true;

            if (particleHandler.isCancelled()) {
                Logger.debug("onForceFieldExtraClick | Removed Force Field Particles.");
                Lang.send(target, Lang.colorize("&6Force Field &7timer has ran out, &6Force Field &7removed."));
                taskRunning = false;
            }

        } else {
            Logger.debug("onForceFieldExtraClick | No Force Field Extra was clicked, return.");
            return;
        }
        target.closeInventory();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnedParticle(EntityDamageEvent damageEvent){
        //if (!taskRunning){
          //  Logger.debug("onSpawnedParticle | Particle task is not running, return.");
            //return;
        //}

        if (!(damageEvent instanceof Player)) {
            Logger.debug("onSpawnedParticle | Damaged entity is not an instance of a player, return.");
            return;
        }

        //target = (Player) damageEvent.getEntity();
        //damageEvent.setCancelled(true);
        //target.setHealth(5000);

        /* Check for correct damage type */
        if (
                damageEvent.getCause() == EntityDamageEvent.DamageCause.FALL
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.LAVA
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.CONTACT
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.CRAMMING
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.DROWNING
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.DRYOUT
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.FIRE
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.LIGHTNING
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.MAGIC
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.MELTING
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.POISON
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.STARVATION
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.SUICIDE
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.THORNS
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.VOID
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.WITHER
                || damageEvent.getCause() == EntityDamageEvent.DamageCause.CUSTOM && damageEvent.getCause() == EntityDamageEvent.DamageCause.valueOf("")) {
            damageEvent.setCancelled(true);
            final Player target = (Player) damageEvent.getEntity();
            final Runnable incrementHealth = () -> target.setHealth(
                    Math.min(target.getHealth() + 1, target.getMaxHealth())
            );

        }

        //if (damageEvent.getCause() == EntityDamageEvent.DamageCause.FALL){
        //    damageEvent.setDamage(1);
        //}
    }

    /**
     * Set Force Field Weapon Extra Type
     * @param forceFieldExtraName Force Field Extra Name
     * @param player Player who selected Force Field
     */
    private void setForceFieldWeaponExtraType(String[] forceFieldExtraName, Player player){
        ItemStack forceFieldType;
        String forceFieldTitle;
        List<String> forceFieldLore;
        int invSlot;

        // INFO: Create Force Field Extras Inventory
        CustomInventories forceFieldInventory = new CustomInventories(Lang.FORCE_FIELD_EXTRA_INVENTORY_TITLE, 18, event -> {
            // NOTICE: Do nothing, yet
        }, plugin);

        // TODO: Add more force field secondary abilities
        for (String gettingForceFieldExtraType : forceFieldExtraName){
            switch (gettingForceFieldExtraType){
                case "standard":
                    // TODO: Nothing
                    invSlot = 0;
                    forceFieldType = new ItemStack(Material.GRAY_DYE, 1);
                    forceFieldTitle = Lang.colorize(Config.FORCE_FIELD_EXTRA_LIST_STANDARD ? Lang.FORCE_FIELD_EXTRA_SELECTED_STANDARD : Lang.FORCE_FIELD_EXTRA_SELECTED_STANDARDNO);
                    forceFieldLore = new ArrayList<>();
                    if (Config.FORCE_FIELD_EXTRA_LIST_STANDARD){
                        if (Lang.FORCE_FIELD_EXTRA_LORE_STANDARD.contains(";")){
                            String[] newLine = Lang.FORCE_FIELD_EXTRA_LORE_STANDARD.split(";");
                            for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore){
                                forceFieldLore.add(Lang.colorize(newLine[newLineLore]));
                            }
                        } else {
                            forceFieldLore.add(Lang.colorize(Lang.FORCE_FIELD_EXTRA_LORE_STANDARD));
                        }
                    } else {
                        forceFieldLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.FORCE_FIELD_EXTRA_SELECTED_STANDARD)));
                    }
                    forceFieldInventory.setToolOrWeapon(invSlot, forceFieldType, forceFieldTitle, forceFieldLore);
                case "knockback":
                    invSlot = 1;
                    forceFieldType = new ItemStack(Material.DIAMOND_SWORD, 1);
                    forceFieldTitle = Lang.colorize(Config.FORCE_FIELD_EXTRA_LIST_KNOCKBACK ? Lang.FORCE_FIELD_EXTRA_SELECTED_KNOCKBACK : Lang.FORCE_FIELD_EXTRA_SELECTED_KNOCKBACKNO);
                    forceFieldLore = new ArrayList<>();
                    if (Config.FORCE_FIELD_EXTRA_LIST_KNOCKBACK){
                        if (Lang.FORCE_FIELD_EXTRA_LORE_KNOCKBACK.contains(";")){
                            String[] newLine = Lang.FORCE_FIELD_EXTRA_LORE_KNOCKBACK.split(";");
                            for (int newLineLore = 0; newLineLore < newLine.length; ++newLineLore){
                                forceFieldLore.add(Lang.colorize(newLine[newLineLore]));
                            }
                        } else {
                            forceFieldLore.add(Lang.colorize(Lang.FORCE_FIELD_EXTRA_LORE_KNOCKBACK));
                        }
                    } else {
                        forceFieldLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.FORCE_FIELD_EXTRA_SELECTED_KNOCKBACKNO)));
                    }
                    forceFieldInventory.setToolOrWeapon(invSlot, forceFieldType, forceFieldTitle, forceFieldLore);
                    break;
                default:
                    break;
            }
        }
        forceFieldInventory.openInventory(player);
    }


    /**
     * Set Force Field Extras into custom inventory
     *
     * @param forceFieldExtraName Force field Extra Name
     * @param player Player who activated Force Field
     */
    private void setForceFieldExtra(String forceFieldExtraName, Player player) {
        ItemStack weapon;
        ItemMeta weaponItemMeta;
        List<String> weaponLore = new ArrayList<>();


        //for (String gettingForceFieldExtraName : forceFieldExtraName) {
            switch (forceFieldExtraName) {
                case "standard":
                    weapon = new ItemStack(Material.GRAY_DYE, 1);
                    weaponItemMeta = weapon.getItemMeta();
                    weaponItemMeta.setDisplayName(Lang.colorize(Lang.FORCE_FIELD_EXTRA_SELECTED_STANDARD));
                    weaponItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    weaponItemMeta.setCustomModelData((int) 096001F); // TODO: Make int config option
                    weaponLore.add(""); // TODO: Add configurable lore settings
                    weaponLore.add("");
                    weaponLore.add("");
                    weaponLore.add("");
                    weaponItemMeta.setLore(weaponLore);
                    weapon.setItemMeta(weaponItemMeta);
                    break;
                case "knockback":
                    weapon = new ItemStack(Material.DIAMOND_SWORD, 1);
                    weaponItemMeta = weapon.getItemMeta();
                    weaponItemMeta.setDisplayName(Lang.colorize(Lang.FORCE_FIELD_EXTRA_SELECTED_STANDARD));
                    weaponItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    weaponItemMeta.setCustomModelData((int) 096001F); // TODO: Make int config option
                    weaponLore.add(""); // TODO: Add configurable lore settings
                    weaponLore.add("");
                    weaponLore.add("");
                    weaponLore.add("");
                    weaponItemMeta.setLore(weaponLore);
                    weapon.setItemMeta(weaponItemMeta);
                    break;
                default:
                    break;
            }
        //}
        //
    }

}
