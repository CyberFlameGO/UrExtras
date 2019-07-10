package net.pl3x.bukkit.urextras.command;

import java.util.HashMap;
import java.util.List;
import net.pl3x.bukkit.urextras.UrExtras;
import net.pl3x.bukkit.urextras.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/*
 * Created by madmac on 9/19/15.
 *
 * float offsetX: The amount of blocks that the effect can stray in X
 * float offsetY: The amount of blocks that the effect can stray in Y
 * float offsetZ: The amount of blocks that the effect can stray in Z
 * float speed: How fast you want it to appear
 * int amount: How many of these you want to show up
 * Location center: player.getLocation().add(0, 2, 0)   -> Puts heart two blocks above their head
 * double range: The visibility (How many blocks until they can't see it)
 *
 * ParticleEffect.HEART.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range);
 *
 */

/**
 * Particle test command
 * <p>
 * Use this to test the particle before applying to features
 */

public class CmdTester implements TabExecutor {
    private UrExtras plugin;
    private int numberArgs;
    private boolean isRunning;
    private boolean isRunningTime;
    private BukkitTask taskToCancel;
    private BukkitTask taskCoolDownToCancel;
    private HashMap<Player, Integer> coolDownTime = new HashMap<>();

    public CmdTester(UrExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    /**
     * Particle test command
     *
     * Custom particle will appear for X seconds
     * Test out particle design/display
     *
     * @param sender Player Applies particle effect around player
     * @param command Create custom particle
     * @param label Check for command name or aliases
     * @param args Sets time length for particle display
     * @return Custom particle effect
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.urextras.tester")){
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player target;

        if(args.length < 2){
            target = (Player) sender;
        }else {
            target = Bukkit.getPlayer(args[1]);

            if(target == null){
                Lang.send(target,"&6Could not find player!");
                return true;
            }
        }

        if(args.length < 1){
            Lang.send(target, "Please enter a time length of 1 - 60 seconds."); //command.getDescription()  <--- This will get command description from plugin .yml
            return true;
        }

        try{
            numberArgs = Integer.valueOf(args[0]);
        }catch (NumberFormatException numberFormatException){
            Lang.send(target, "&6Please enter a time length of &71 &6- &760&6!");
            return true;
        }

        if(numberArgs < 0 || numberArgs > 60){
            Lang.send(target, "&6Please enter a time length of &71 &6- &760&6!");
            return true;
        }

        if(coolDownTime.containsKey(target)){
            target.sendMessage("&6You can not use this command for another &7" + coolDownTime.get(target) + " &6seconds!");
            return true;
        }
        if(isRunning){
            target.sendMessage("&6This command was already activated!");
            return true;
        }

        int num1 = numberArgs;
        int num2 = 0;

        if(num1 < num2) return true;

        if(!isRunningTime) {
            if (!coolDownTime.containsKey(target)) {

                /*  INFO: target.getLocation().add(0,1.3,1),  unwanted effects */
                final BukkitTask testOne = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

                    /*
                    target.spawnParticle(
                            Particle.DRIP_LAVA,
                            target.getLocation().add(0, 1.3, 1),
                            4,
                            0.2F,
                            0.0F,
                            0.2F,
                            0.2F);

                    target.spawnParticle(
                            Particle.CLOUD,
                            target.getLocation().add(0, 1.3, 1),
                            5,
                            0.01F, // INFO: Thickness Horizontal
                            0.01F, // INFO: Thickness up and down
                            0.01F, // INFO: Thickness diagonal
                            0.02F); // INFO: Spawn Speed ?

                    /*
                    for (int degree = 0; degree < 360; degree++) {
                        double radians = Math.toRadians(degree);
                        double x = Math.cos(radians);
                        double z = Math.sin(radians);
                        target.getLocation().add(x,0,z);
                        target.getLocation().getWorld().playEffect(target.getLocation(), Effect.SMOKE, 1);
                        target.getLocation().subtract(x,0,z);
                    }
                    */

                    /*
                     *
                     * INFO: God mode protection sphere effect
                     */
                    for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                        double radius = Math.sin(i);
                        double y = Math.cos(i);
                        for (double a = 0; a < Math.PI * 2; a+= Math.PI / 60) {
                            double x = Math.cos(a) * radius;
                            double z = Math.sin(a) * radius;
                            Location playerLoc = target.getLocation().add(x, y, z);
                            //target.spawnParticle(Particle.DOLPHIN, playerLoc.add(0,1,0),1); // cool bool effect
                            target.spawnParticle(Particle.LAVA, playerLoc.add(0,1,0),1); //
                            target.getLocation().subtract(x, y, z);
                        }
                    }

                }, 0L, 2L);

                taskToCancel = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if (numberArgs >= 1) {
                        isRunning = true;
                    } else {
                        isRunning = false;
                    }

                    if (isRunning) {
                        numberArgs--;

                        testOne.getTaskId();
                    }

                    if (numberArgs <= 0 || !target.isOnline() || !isRunning) {

                        testOne.cancel();

                        target.sendMessage("&6Your command has ran out of time");

                        taskToCancel.cancel();

                        isRunning = false;

                        runCoolDown(target);
                    }
                }, 0L, 20L);
            }
            return true;
        }
        return true;
    }

    /**
     * Create cooldown
     *
     * Cooldown is created as a BukkitTask
     *
     * @param player Applies cooldown to player that activated the test command
     */
    public void runCoolDown(Player player){
        final Player target = player;

        if(!isRunning) {
            coolDownTime.put(target, 5); // cool down for 20 seconds
            taskCoolDownToCancel = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                isRunningTime = true;

                coolDownTime.put(target, coolDownTime.get(target) - 1);

                if(coolDownTime.get(target) <= 0) {
                    coolDownTime.remove(target);

                    target.sendMessage("&6Your command cooldown has expired.");

                    isRunningTime = false;

                    taskCoolDownToCancel.cancel();
                }
            }, 0L, 20L);
        }
    }



}
