package net.pl3x.bukkit.urextras;

import net.pl3x.bukkit.urextras.command.CmdUrExtrasPortal;
import net.pl3x.bukkit.urextras.configuration.Lang;
import net.pl3x.bukkit.urextras.listener.TreeeListPortalClickListener;
import net.pl3x.bukkit.urextras.listener.UrExtrasPortalClickListener;
import net.pl3x.bukkit.urextras.command.CmdTester;
import net.pl3x.bukkit.urextras.command.CmdReload;
import net.pl3x.bukkit.urextras.configuration.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * UrExtras Plugin
 * <p>
 * Plugin author: DwDMadMac
 * <p>
 * This will give players the option to have custom tools or weapons
 */
public class UrExtras extends JavaPlugin {
    private static UrExtras instance;

    public UrExtras() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        getServer().getPluginManager().registerEvents(new CmdUrExtrasPortal(this),this);
        getServer().getPluginManager().registerEvents(new UrExtrasPortalClickListener(this), this);
        getServer().getPluginManager().registerEvents(new TreeeListPortalClickListener(this),this);

        getCommand("urextras").setExecutor(new CmdReload(this));
        getCommand("urextrasportal").setExecutor(new CmdUrExtrasPortal(this));
        getCommand("tester").setExecutor(new CmdTester(this));

        //Metrics metrics = new Metrics(this);
        //metrics.addCustomChart(new Metrics.SimplePie("simple_pie_chart", () -> "Simple Pie Chart"));
        Logger.info(getName() + " v" + UrExtras.getInstance().getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable(){
        Logger.info(getName() + " disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4[&7ERROR&4]&c " + getName() + " is disabled. See console log for more information."));
        return true;
    }

    public static UrExtras getInstance() {
        return instance;
    }
}
