package net.pl3x.bukkit.urextras.util;

import net.pl3x.bukkit.urextras.Logger;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Particles
 * <p>
 * Create particle effect as a BukkitRunnable
 */
public class Particles extends BukkitRunnable {
    private final Player player;
    private Particle particle;
    private boolean cancel;

    /**
     * Gets the player that enabled the particle and set the particle
     * that is configured inside config file
     *
     * @param player Player who enabled particle
     * @param particle Spawning particle
     */
    public Particles(Player player, Particle particle) {
        this.player = player;

        Particle particleType = null;

        try {
            //particleType = Particle.valueOf(Config.PARTICLE_TYPE_DEFAULT.toUpperCase());
            particleType = particle;
        } catch (IllegalArgumentException ex2){
            Logger.error("Particle type invalid. Cancelling particle task.");
            setCancel(true);
        }
        setParticle(particleType);
    }

    /**
     * Get the Particle Type
     *
     * @return Particle Type
     */
    public Particle getParticle(){
        return particle;
    }

    /**
     * Set the Particle Type
     *
     * @param particle Particle Type
     */
    public void setParticle(Particle particle){
        this.particle = particle;
    }

    /**
     * Check if particle is cancelled
     *
     * @return true if cancelled
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Set to cancel particle
     *
     * @param cancel set cancel
     */
    public void setCancel(boolean cancel){
        this.cancel = cancel;
    }

    /**
     * Check for Particle Types that will crash the client
     *
     * @return Particle Type
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
     * Make cancel true
     */
    @Override
    public void cancel() {
        cancel = true;
        super.cancel();
    }

    /**
     *Rune particle effect
     */
    @Override
    public void run() {
        if (stopClientCrash()){
            Logger.error("This particle is known to crash clients!");
            Logger.error("Cancelling particle spawning!");
            cancel();
            return;
        }

        if (particle.equals(Particle.PORTAL)){
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
        } else if (particle.equals(Particle.DOLPHIN)){
            for (double i = 0; i <= Math.PI; i += Math.PI / 10){
                double radius = Math.sin(i);
                double y = Math.cos(i);
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 60) {
                    double x = Math.cos(a) * radius;
                    double z = Math.sin(a) * radius;
                    Location playerLoc = player.getLocation().add(x, y, z);
                    player.spawnParticle(getParticle(), playerLoc.add(0, 1, 0), 1);
                    player.getLocation().subtract(x, y, z);
                }

            }

            /*
            FORCE_FIELD_PARTICLE:
            for (int particleSpawnerTimer = 0; particleSpawnerTimer < Config.FORCE_FIELD_WEAPON_TIMER; particleSpawnerTimer++) {
                for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                    double radius = Math.sin(i);
                    double y = Math.cos(i);
                    for (double a = 0; a < Math.PI * 2; a += Math.PI / 60) {
                        double x = Math.cos(a) * radius;
                        double z = Math.sin(a) * radius;
                        Location playerLoc = player.getLocation().add(x, y, z);
                        player.spawnParticle(getParticle(), playerLoc.add(0, 1, 0), 1);
                        player.getLocation().subtract(x, y, z);
                        if (particleSpawnerTimer >= Config.FORCE_FIELD_WEAPON_TIMER){
                            Logger.debug("onsetForceFieldExtra | Force field particle 'ForLoop' removed");
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                            break FORCE_FIELD_PARTICLE;
                        }
                    }
                }
            }*/
            // NOTICE: if/else statement end
        }
    }
}
