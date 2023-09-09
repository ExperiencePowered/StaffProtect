package net.experience.powered.staffprotect.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Authorizer {

    private final static Executor executor = Executors.newWorkStealingPool();
    private final static ConcurrentMap<UUID, Long> authorized = new ConcurrentHashMap<>();

    public static @NotNull CompletableFuture<Boolean> isAuthorized(final @NotNull Player player) {
        return CompletableFuture.supplyAsync(new Supplier<>() {
            private int tries = 0;

            @Override
            public Boolean get() {
                while (tries < 15) {
                    if (authorized.containsKey(player.getUniqueId())) {
                        authorized.remove(player.getUniqueId());
                        return true;
                    }
                    try {
                        tries++;
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return false;
            }
        }, executor);
    }

    public static void authorize(final @NotNull Player player) {
        authorized.put(player.getUniqueId(), Instant.now().toEpochMilli());
    }
}
