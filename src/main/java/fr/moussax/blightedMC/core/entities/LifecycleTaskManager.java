package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Per-entity lifecycle task manager. Allows registering repeating and delayed tasks
 * that are automatically scheduled on entity spawn/attach and canceled on kill.
 */
public final class LifecycleTaskManager {
  private final List<ScheduledTask> tasks = new ArrayList<>();

  public void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
    tasks.add(new ScheduledTask(factory, delayTicks, periodTicks, true));
  }

  public void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
    tasks.add(new ScheduledTask(factory, delayTicks, 0L, false));
  }

  public void scheduleAll() {
    for (ScheduledTask task : tasks) {
      task.schedule();
    }
  }

  public void scheduleLast() {
    if (tasks.isEmpty()) return;
    tasks.getLast().schedule();
  }

  public void cancelAll() {
    for (ScheduledTask task : tasks) {
      task.cancel();
    }
  }

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

    private void schedule() {
      cancel();
      current = factory.get();
      if (repeating) {
        current.runTaskTimer(BlightedMC.getInstance(), delayTicks, periodTicks);
      } else {
        current.runTaskLater(BlightedMC.getInstance(), delayTicks);
      }
    }

    private void cancel() {
      if (current != null) {
        try {
          current.cancel();
        } catch (IllegalStateException ignored) {}
        current = null;
      }
    }
  }
}
