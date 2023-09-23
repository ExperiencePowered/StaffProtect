package net.experience.powered.staffprotect.database;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class DatabaseProperties {

    private static final ConcurrentMap<String, DatabaseProperties> instances = new ConcurrentHashMap<>();
    private final HashMap<String, Object> properties = new HashMap<>();

    // Used in case property is null, string which is in consumer is property
    private Consumer<String> consumer;

    private DatabaseProperties() {
        this("Unknown");
    }

    public DatabaseProperties(final @NotNull String databaseName) {
        instances.put(databaseName, this);
    }

    public void initializeNullRunnable(final @NotNull Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void write(final @NotNull String property, final @NotNull Object obj) {
        properties.put(property, obj);
    }

    public void remove(final @NotNull String property) {
        properties.remove(property);
    }

    public Object getProperty(final @NotNull String property) {
        final Object obj = properties.get(property);
        if (obj == null) {
            if (consumer != null) {
                consumer.accept(property);
            }
            return null;
        }
        return obj;
    }

    public static DatabaseProperties getInstance(final @NotNull String databaseName) {
        return instances.get(databaseName);
    }

    @Override
    public String toString() {
        return "DatabaseProperties{" + "properties=" + properties +
                ", consumer=" + consumer +
                '}';
    }
}
