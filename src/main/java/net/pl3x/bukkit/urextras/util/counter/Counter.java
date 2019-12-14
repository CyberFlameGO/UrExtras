package net.pl3x.bukkit.urextras.util.counter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Counter {
    private final ScheduledExecutorService scheduler;
    private final long initialDelay;
    private final long delayBetweenBeeps;
    private final long shutdownAfter;
    /* If invocations might overlap, you can specify more  than a single thread. */
    private static final int NUM_THREADS = 1;

    /**
     * Creates a timer task
     *
     * @param initialDelay Delay which the task will start
     * @param delayBetweenBeeps The delay which the task will beep
     * @param stopAfter Stops the task after X seconds
     */
    public Counter(long initialDelay, long delayBetweenBeeps, long stopAfter){
        this.initialDelay = initialDelay;
        this.delayBetweenBeeps = delayBetweenBeeps;
        this.shutdownAfter = stopAfter;
        this.scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
    }

    /**
     * When called will stop the task gracefully
     */
    public void activatethenStop(){
        Runnable counterTask = new CounterTask();
        ScheduledFuture<?> counterFuture = scheduler.scheduleWithFixedDelay(counterTask, initialDelay, delayBetweenBeeps, TimeUnit.SECONDS);
        Runnable stopCounter = new StopCounterTask(counterFuture);
        scheduler.shutdown();
        scheduler.schedule(stopCounter, shutdownAfter, TimeUnit.SECONDS);
    }















/*
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private ScheduledExecutorService scheduledExecutorService;

    public Counter(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public static void Counter(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void stopCounter(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Logger.error("termination interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                Logger.error("killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }

    public void increment(){
        atomicInteger.incrementAndGet();
    }

    public void decrement(){
        atomicInteger.decrementAndGet();
    }

    public int value(){
        return atomicInteger.get();
    }
*/
}