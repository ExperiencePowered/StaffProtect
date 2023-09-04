package net.experience.powered.staffprotect.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public final class MySQL extends AbstractDatabase {
    private HikariDataSource hikari;

    public MySQL(@NotNull HikariConfig hikariConfig) {
        super(hikariConfig);
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
