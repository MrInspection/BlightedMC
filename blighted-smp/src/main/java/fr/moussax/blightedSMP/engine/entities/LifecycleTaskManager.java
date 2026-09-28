package fr.moussax.blightedSMP.engine.entities;

import fr.moussax.blightedSMP.BlightedSMP;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Manages scheduler tasks bound to the lifecycle of a {@link BlightedEntity}.
 * <p>
 * Allows entities to register delayed or repeating tasks that are automatically
 * scheduled on initialization and canceled on destruction.
 */
public final class LifecycleTaskManager {

    private List<ScheduledTask> tasks;

    /**
     * Adds a repeating task using a {@link Runnable} action.
     *
     * @param action      task action
     * @param delayTicks  initial delay in ticks
     * @param periodTicks execution interval in ticks
     */
    public void addRepeatingTask(Runnable action, long delayTicks, long periodTicks) {
        ensureList();
        tasks.add(new ScheduledTask(action, null, delayTicks, periodTicks, true));
    }

    /**
     * Adds a delayed task using a {@link Runnable} action.
     *
     * @param action     task action
     * @param delayTicks delay in ticks
     */
    public void addDelayedTask(Runnable action, long delayTicks) {
        ensureList();
        tasks.add(new ScheduledTask(action, null, delayTicks, 0L, false));
    }

    /**
     * Adds a repeating task using a {@link BukkitRunnable} supplier (for backwards compatibility).
     *
     * @param factory     supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks  initial delay before the first execution, in ticks
     * @param periodTicks interval between consecutive runs, in ticks
     */
    public void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
        ensureList();
        tasks.add(new ScheduledTask(null, factory, delayTicks, periodTicks, true));
    }

    /**
     * Adds a delayed task using a {@link BukkitRunnable} supplier (for backwards compatibility).
     *
     * @param factory    supplier creating a new {@link BukkitRunnable} instance
     * @param delayTicks delay before execution, in ticks
     */
    public void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
        ensureList();
        tasks.add(new ScheduledTask(null, factory, delayTicks, 0L, false));
    }

    /**
     * Schedules all registered tasks.
     */
    public void scheduleAll() {
        if (tasks == null) return;
        for (ScheduledTask task : new ArrayList<>(tasks)) {
            task.schedule(this);
        }
    }

    /**
     * Schedules only the most recently added task.
     */
    public void scheduleLast() {
        if (tasks == null || tasks.isEmpty()) return;
        tasks.getLast().schedule(this);
    }

    /**
     * Cancels all currently running tasks associated with this manager.
     */
    public void cancelAll() {
        if (tasks == null) return;
        for (ScheduledTask task : new ArrayList<>(tasks)) {
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

    private static final class ScheduledTask {
        private final Runnable action;
        private final Supplier<BukkitRunnable> factory;
        private final long delayTicks;
        private final long periodTicks;
        private final boolean repeating;
        private BukkitTask currentTask;
        private BukkitRunnable currentRunnable;

        private ScheduledTask(Runnable action, Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks, boolean repeating) {
            this.action = action;
            this.factory = factory;
            this.delayTicks = delayTicks;
            this.periodTicks = periodTicks;
            this.repeating = repeating;
        }

        private void schedule(LifecycleTaskManager manager) {
            cancel();

            if (repeating) {
                if (action != null) {
                    currentTask = Bukkit.getScheduler().runTaskTimer(BlightedSMP.getInstance(), action, delayTicks, periodTicks);
                } else if (factory != null) {
                    currentRunnable = factory.get();
                    currentTask = currentRunnable.runTaskTimer(BlightedSMP.getInstance(), delayTicks, periodTicks);
                }
            } else {
                if (action != null) {
                    currentTask = Bukkit.getScheduler().runTaskLater(BlightedSMP.getInstance(), () -> {
                        try {
                            action.run();
                        } finally {
                            manager.onTaskComplete(ScheduledTask.this);
                        }
                    }, delayTicks);
                } else if (factory != null) {
                    currentRunnable = new BukkitRunnable() {
                        private final BukkitRunnable inner = factory.get();

                        @Override
                        public void run() {
                            try {
                                if (inner != null) inner.run();
                            } finally {
                                manager.onTaskComplete(ScheduledTask.this);
                            }
                        }
                    };
                    currentTask = currentRunnable.runTaskLater(BlightedSMP.getInstance(), delayTicks);
                }
            }
        }

        private void cancel() {
            if (currentTask != null) {
                try {
                    currentTask.cancel();
                } catch (Exception _) {
                }
                currentTask = null;
            }
            if (currentRunnable != null) {
                try {
                    if (!currentRunnable.isCancelled()) {
                        currentRunnable.cancel();
                    }
                } catch (Exception _) {
                }
                currentRunnable = null;
            }
        }
    }
}
