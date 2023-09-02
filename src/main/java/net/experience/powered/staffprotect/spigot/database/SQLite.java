package net.experience.powered.staffprotect.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SQLite extends AbstractDatabase {
    private Connection connection;
    private final File file;

    public SQLite(final @NotNull HikariConfig hikariConfig, final @NotNull File file) {
        super(hikariConfig);
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

        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            this.connection = null;
        }

    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        return this.connection;
    }
}
