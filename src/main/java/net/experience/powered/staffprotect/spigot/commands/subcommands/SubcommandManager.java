package net.experience.powered.staffprotect.spigot.commands.subcommands;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SubcommandManager {

    private final static ConcurrentMap<Class<? extends Subcommand>, Subcommand> hashMap = new ConcurrentHashMap<>();

    public static void register(final @NotNull Class<? extends Subcommand> clazz, Subcommand subcommand) {
        hashMap.put(clazz, subcommand);
    }

    public static void unregister(final @NotNull Class<? extends Subcommand> clazz) {
        hashMap.remove(clazz);
    }

    public static Subcommand getSubcommand(final @NotNull Class<? extends Subcommand> clazz) {
        Subcommand subcommand = hashMap.get(clazz);
        if (subcommand == null) {
            try {
                Constructor<? extends Subcommand> constructor = clazz.getDeclaredConstructor();
                Subcommand newInstance = constructor.newInstance();
                register(clazz, newInstance);
                return newInstance;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return hashMap.get(clazz);
    }
}
