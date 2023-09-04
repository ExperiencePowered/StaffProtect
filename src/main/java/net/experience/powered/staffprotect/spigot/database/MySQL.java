package net.experience.powered.staffprotect.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MySQL extends AbstractDatabase {
    private HikariDataSource hikari;

    public MySQL(@NotNull HikariConfig hikariConfig) {
        super(hikariConfig);
    }

    @Override
    public void connect() {
        hikari = new HikariDataSource(hikariConfig);

        try {
            assert getConnection() != null;
            PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            try {
                assert getConnection() != null;
                getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            this.hikari = null;
        }
    }

    @Override
    public boolean isConnected() {
        return hikari != null;
    }

    @Override
    public Connection getConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
