package net.pl3x.bukkit.urextras.util;

import net.pl3x.bukkit.urextras.Logger;
import net.pl3x.bukkit.urextras.configuration.Config;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 */
public class Particles extends BukkitRunnable {
    private final Player player;
    private Particle particle;
    private boolean cancel;

    /**
     *
     * @param player
     */
    public Particles(Player player) {
        this.player = player;

        Particle particleType = null;

        try {
            particleType = Particle.valueOf(Config.PARTICLE_TYPE.toUpperCase());
        } catch (IllegalArgumentException ex2){
            Logger.error("Particle type invalid. Cancelling particle task.");
            setCancel(true);
        }
        setParticle(particleType);
    }

    /**
     *
     * @return
     */
    public Particle getParticle(){
        return particle;
    }

    /**
     *
     * @param particle
     */
    public void setParticle(Particle particle){
        this.particle = particle;
    }

    /**
     *
     * @return
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     *
     * @param cancel
     */
    public void setCancel(boolean cancel){
        this.cancel = cancel;
    }

    /**
     *
     * @return
     */
    public boolean stopClientCrash(){
        switch (getParticle()){
            case ITEM_CRACK:
            case BLOCK_CRACK:
            case BLOCK_DUST:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     */
    @Override
    public void cancel() {
        cancel = true;
        super.cancel();
    }

    /**
     *
     */
    @Override
    public void run() {
        if (stopClientCrash()){
            Logger.error("This particle is known to crash clients!");
            Logger.error("Cancelling particle spawning!");
            cancel();
            return;
        }

        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 4) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                Location playerLoc = player.getLocation().add(x, y, z);
                player.getWorld().spawnParticle(getParticle(), playerLoc.add(0, 1, 0), 1);
            }
        }
    }

}
