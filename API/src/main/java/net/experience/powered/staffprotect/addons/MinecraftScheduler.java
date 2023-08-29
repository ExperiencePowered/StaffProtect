package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MinecraftScheduler {

    private final BukkitTask globalTask;
    private final JavaPlugin javaPlugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final Set<BukkitTask> tasks;

    public MinecraftScheduler(final @NotNull Set<BukkitTask> tasks) {
        this.tasks = tasks;
        this.javaPlugin = StaffProtect.getInstance().getPlugin();
        this.globalTask = runTaskTimer(() -> tasks.removeIf(BukkitTask::isCancelled), 0L, 1L);
    }

    public BukkitTask getGlobalTask() {
        return globalTask;
    }

    @NotNull
    public BukkitTask runTask(final @NotNull Runnable runnable) {
        return scheduler.runTask(javaPlugin, runnable);
    }

    @NotNull
    public BukkitTask runTaskAsynchronously(final @NotNull Runnable runnable) {
        return scheduler.runTaskAsynchronously(javaPlugin, runnable);
    }

    @NotNull
    public BukkitTask runTaskLater(final @NotNull Runnable runnable, long delay) {
        final BukkitTask task = scheduler.runTaskLater(javaPlugin, runnable, delay);
        tasks.add(task);
        return task;
    }

    @NotNull
    public BukkitTask runTaskLaterAsynchronously(final @NotNull Runnable runnable, long delay) {
        final BukkitTask task = scheduler.runTaskLaterAsynchronously(javaPlugin, runnable, delay);
        tasks.add(task);
        return task;
    }

    @NotNull
    public BukkitTask runTaskTimer(final @NotNull Runnable runnable, long delay, long period) {
        final BukkitTask task = scheduler.runTaskTimer(javaPlugin, runnable, delay, period);
        tasks.add(task);
        return task;
    }

    @NotNull
    public BukkitTask runTaskTimerAsynchronously(final @NotNull Runnable runnable, long delay, long period) {
        final BukkitTask task = scheduler.runTaskTimerAsynchronously(javaPlugin, runnable, delay, period);
        tasks.add(task);
        return task;
    }
}
