package fr.moussax.blightedMC.smp.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Manages scheduler tasks bound to the lifecycle of an {@link AbstractBlightedEntity}.
 * <p>
 * Allows entities to register delayed or repeating {@link BukkitRunnable} tasks that
 * are automatically scheduled on initialization and canceled on destruction.
 * Tasks are executed through the {@link BlightedMC} plugin scheduler and can be
 * rescheduled as needed during the entity’s lifetime.
 */
public final class LifecycleTaskManager {

    /**
     * Holds all registered scheduled tasks for the associated entity.
     */
    private final List<ScheduledTask> tasks = new ArrayList<>();

    /**
     * Adds a repeating task that executes periodically for the entity lifecycle.
     *
     * @param factory     supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks  initial delay before the first execution, in ticks
     * @param periodTicks interval between consecutive runs, in ticks
     */
    public void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
        tasks.add(new ScheduledTask(factory, delayTicks, periodTicks, true));
    }

    /**
     * Adds a delayed one-time task that runs once after the given delay.
     *
     * @param factory    supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks delay before execution, in ticks
     */
    public void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
        tasks.add(new ScheduledTask(factory, delayTicks, 0L, false));
    }

    /**
     * Schedules all registered tasks. This is typically invoked
     * during the entity’s runtime initialization phase.
     */
    public void scheduleAll() {
        for (ScheduledTask task : tasks) {
            task.schedule();
        }
    }

    /**
     * Schedules only the most recently added task.
     * Useful for adding new runtime behaviors dynamically.
     */
    public void scheduleLast() {
        if (tasks.isEmpty()) return;
        tasks.getLast().schedule();
    }

    /**
     * Cancels all currently running scheduled tasks associated with the entity.
     * This should be invoked during entity destruction or killing.
     */
    public void cancelAll() {
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
    }

    /**
     * Represents a scheduled Bukkit task bound to an entity’s lifecycle.
     * Handles its own scheduling, cancellation, and re-initialization logic.
     */
    private static final class ScheduledTask {
        private final Supplier<BukkitRunnable> factory;
        private final long delayTicks;
        private final long periodTicks;
        private final boolean repeating;
        private BukkitRunnable current;

        /**
         * Constructs a new scheduled task definition.
         *
         * @param factory     the runnable supplier
         * @param delayTicks  delay before first execution
         * @param periodTicks interval between runs (ignored for non-repeating)
         * @param repeating   whether this task repeats
         */
        private ScheduledTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks, boolean repeating) {
            this.factory = factory;
            this.delayTicks = delayTicks;
            this.periodTicks = periodTicks;
            this.repeating = repeating;
        }

        /**
         * Starts or restarts this task on the Bukkit scheduler.
         * Cancels any previous instance before scheduling a new one.
         */
        private void schedule() {
            cancel();
            current = factory.get();
            if (repeating) {
                current.runTaskTimer(BlightedMC.getInstance(), delayTicks, periodTicks);
            } else {
                current.runTaskLater(BlightedMC.getInstance(), delayTicks);
            }
        }

        /**
         * Cancels the currently running task instance, if active.
         * Ignores cancellation errors due to already terminated tasks.
         */
        private void cancel() {
            if (current != null) {
                try {
                    current.cancel();
                } catch (IllegalStateException ignored) {
                }
                current = null;
            }
        }
    }
}
