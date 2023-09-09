package net.experience.powered.staffprotect.spigot.database;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SQLite extends AbstractDatabase {
    private final File file;

    public SQLite(final @NotNull DatabaseProperties properties, final @NotNull File file) {
        super(properties);
        this.file = file;
    }

    public void connect() {
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
        try (PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        disconnect();
    }
}
