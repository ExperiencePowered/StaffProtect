package net.experience.powered.staffprotect.spigot.database;

import net.experience.powered.staffprotect.database.AbstractDatabase;
import net.experience.powered.staffprotect.database.DatabaseProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SQLite extends AbstractDatabase {
    private final File file;

    public SQLite(final @Nullable DatabaseProperties properties, final @NotNull File file) {
        super(properties);
        this.file = file;
    }

    public SQLite(final @NotNull DatabaseProperties properties) {
        this(properties, (File) properties.getProperty("file"));
    }

    public void connect() {
        properties.initializeNullRunnable((string) -> {
            throw new IllegalStateException("Property " + string + " is null.");
        });

        try {
            Class.forName("org.sqlite.SQLiteDataSource");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.file);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
