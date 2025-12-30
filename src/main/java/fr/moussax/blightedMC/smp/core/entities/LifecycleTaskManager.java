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
    private List<ScheduledTask> tasks;

    /**
     * Adds a repeating task that executes periodically for the entity lifecycle.
     *
     * @param factory     supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks  initial delay before the first execution, in ticks
     * @param periodTicks interval between consecutive runs, in ticks
     */
    public void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
        ensureList();
        tasks.add(new ScheduledTask(factory, delayTicks, periodTicks, true));
    }

    /**
     * Adds a delayed one-time task that runs once after the given delay.
     *
     * @param factory    supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks delay before execution, in ticks
     */
    public void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
        ensureList();
        tasks.add(new ScheduledTask(factory, delayTicks, 0L, false));
    }

    /**
     * Schedules all registered tasks. This is typically invoked
     * during the entity’s runtime initialization phase.
     */
    public void scheduleAll() {
        if (tasks == null) return;
        for (ScheduledTask task : new ArrayList<>(tasks)) {
            task.schedule(this);
        }
    }

    /**
     * Schedules only the most recently added task.
     * Useful for adding new runtime behaviors dynamically.
     */
    public void scheduleLast() {
        if (tasks == null || tasks.isEmpty()) return;
        tasks.getLast().schedule(this);
    }

    /**
     * Cancels all currently running scheduled tasks associated with the entity.
     * This should be invoked during entity destruction or killing.
     */
    public void cancelAll() {
        if (tasks == null) return;
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
        tasks = null;
    }

    private void ensureList() {
        if (tasks == null) tasks = new ArrayList<>(4);
    }

    private void onTaskComplete(ScheduledTask task) {
        if (tasks == null) return;
        tasks.remove(task);
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

        private ScheduledTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks, boolean repeating) {
            this.factory = factory;
            this.delayTicks = delayTicks;
            this.periodTicks = periodTicks;
            this.repeating = repeating;
        }

        private void schedule(LifecycleTaskManager manager) {
            cancel();

            if (repeating) {
                current = factory.get();
                current.runTaskTimer(BlightedMC.getInstance(), delayTicks, periodTicks);
            } else {
                current = new BukkitRunnable() {
                    private final BukkitRunnable inner = factory.get();

                    @Override
                    public void run() {
                        try {
                            if (inner != null) inner.run();
                        } finally {
                            // Ensure this is removed from the list even if the logic crashes
                            manager.onTaskComplete(ScheduledTask.this);
                        }
                    }
                };
                current.runTaskLater(BlightedMC.getInstance(), delayTicks);
            }
        }

        private void cancel() {
            if (current != null) {
                try {
                    if (!current.isCancelled()) {
                        current.cancel();
                    }
                } catch (IllegalStateException ignored) {
                }
                current = null;
            }
        }
    }
}
