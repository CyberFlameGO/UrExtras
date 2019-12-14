package net.pl3x.bukkit.urextras.util.counter;

import net.pl3x.bukkit.urextras.Logger;

public class CounterTask implements Runnable {
    private int count;

    /**
     * Increase counter time by 1
     */
    @Override
    public void run(){
        ++count;
        Logger.debug("counter " + count);
    }
}
