package net.experience.powered.staffprotect.spigot.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDatabase {

    protected HikariDataSource dataSource;
    protected final DatabaseProperties properties;

    public AbstractDatabase(final @NotNull DatabaseProperties properties) {
        this.properties = properties;
    }

    public abstract void connect();
    public void disconnect() {
        if (this.isConnected()) {
            this.dataSource.close();
            this.dataSource = null;
        }
    }
    public boolean isConnected() {
        return this.dataSource != null;
    }
    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
