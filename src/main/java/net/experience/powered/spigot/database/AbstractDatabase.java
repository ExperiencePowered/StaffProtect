package net.experience.powered.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public abstract class AbstractDatabase {

    protected final HikariConfig hikariConfig;

    public AbstractDatabase(final @NotNull HikariConfig hikariConfig) {
        this.hikariConfig = hikariConfig;
    }

    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean isConnected();
    public abstract Connection getConnection();

}
