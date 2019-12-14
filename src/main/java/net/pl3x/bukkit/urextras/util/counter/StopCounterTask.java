package net.pl3x.bukkit.urextras.util.counter;

import java.util.concurrent.ScheduledFuture;
import net.pl3x.bukkit.urextras.Logger;

public final class StopCounterTask implements Runnable {
    private ScheduledFuture<?> scheduledFuture;
    private static final boolean DONT_INTERRUPT_IF_RUNNING = false;

    /**
     * Grabs the future schduler task
     *
     * @param scheduledFuture The future scheduler task
     */
    StopCounterTask(ScheduledFuture<?> scheduledFuture){
        this.scheduledFuture = scheduledFuture;
    }

    /**
     * Runs a task to stop the Scheduler Task (Counter Task)
     */
    @Override
    public void run(){
        Logger.debug("Stopping Counter.");
        scheduledFuture.cancel(DONT_INTERRUPT_IF_RUNNING);
        /*
         * Note that this Task also performs cleanup,
         * by asking the scheduler to shutdown gracefully.
         */
        // INFO: Scheduler stopped in Counter Class
    }
}
