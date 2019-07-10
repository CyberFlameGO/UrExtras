package net.pl3x.bukkit.urextras.command;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * TODO: Add pumpkins
 * TODO: Add watermelon
 * TODO:
 */

public class CmdUrExtrasPortal implements TabExecutor {

    private UrExtras plugin;

    public CmdUrExtrasPortal(UrExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    /**
     * TODO:
     *   - Add check for players gamemode
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return UrExtras Portal
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

        /* NOTICE: Make UrExtras Portal Inventory */
        Inventory urExtrasPortalInventory = Bukkit.createInventory(null, InventoryType.BARREL, Lang.UREXTRAS_PORTAL_INVENTORY_TITLE);

        /* NOTICE: Close Inventory | Apple */
        ItemStack appleIcon = new ItemStack(Material.APPLE, 1);
        ItemMeta appleIconMeta = appleIcon.getItemMeta();
        appleIconMeta.setDisplayName(Lang.colorize("&4Close Inventory") );
        ArrayList<String> appleIconLore = new ArrayList<>();
        appleIconLore.add(Lang.colorize("&7Click to close Effects") );
        appleIconLore.add(Lang.colorize("&7Portal inventory") );
        appleIconMeta.setLore(appleIconLore);
        appleIcon.setItemMeta(appleIconMeta);
        urExtrasPortalInventory.setItem(0, appleIcon);

        /* NOTICE: Treee Spawner Tool | Diamond Axe */
        ItemStack itemOneIcon = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta itemOneMeta = itemOneIcon.getItemMeta();
        itemOneMeta.setDisplayName(
                    (
                            Config.TREEE_SPAWNER_TOOL_CLICK ?
                                !target.hasPermission("command.urextras.portal.treeespawnertool") ? Lang.colorize(Lang.NO_TREEE_SPAWNER_TOOL) : Lang.colorize(Lang.TREEE_SPAWNER_TOOL)
                            :
                                Lang.colorize(Lang.NO_TREEE_SPAWNER_TOOL)
                    )
                );
        itemOneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ArrayList<String> itemOneLore = new ArrayList<String>();
        itemOneLore.add(Lang.colorize("&8Click to receive your Tool."));
        itemOneLore.add(Lang.colorize(""));

        if (Config.TREEE_SPAWNER_TOOL_CLICK) {
            if (!target.hasPermission("command.urextras.portal.treeespawnertool")) {
                itemOneLore.add(Lang.colorize("&cYou do not have permission to"));
                itemOneLore.add(Lang.colorize("&cuse this feature."));
                itemOneLore.add(Lang.colorize(""));
                itemOneLore.add(Lang.colorize("&7More information coming soon."));
                Logger.debug("onCommand | No permission to treepawnertool, returned");
            } else {
                itemOneLore.add(Lang.colorize("&7When you click on a block with"));
                itemOneLore.add(Lang.colorize("&7the tool given, a custom tree list"));
                itemOneLore.add(Lang.colorize("&7inventory will appear for selection."));
                itemOneLore.add(Lang.colorize(""));
                itemOneLore.add(Lang.colorize("&7Once you select/click a treee of"));
                itemOneLore.add(Lang.colorize("&7choice, it will then spawn on the"));
                itemOneLore.add(Lang.colorize("&7block you just clicked."));
                Logger.debug("onCommand | Target was given a Tree Spawner Tool.");
            }
        } else {
            itemOneLore.add(Lang.colorize(Lang.DISABLED.replace("{getDisabledName}", Lang.NO_TREEE_SPAWNER_TOOL)));
        }

        itemOneMeta.setLore(itemOneLore);
        itemOneIcon.setItemMeta(itemOneMeta);
        urExtrasPortalInventory.setItem(19, itemOneIcon);

        // TODO: Create additional custom tools / weapons
        //   -
        /* NOTICE:  */


        /* NOTICE: Create Inventory */
        target.openInventory(urExtrasPortalInventory);
        /** ==== EFFECTS INVENTORY END ==== **/

        return true;
    }
}
