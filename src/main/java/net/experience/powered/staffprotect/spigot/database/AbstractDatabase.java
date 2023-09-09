package net.experience.powered.staffprotect.spigot.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDatabase {

    protected Connection connection;
    protected final DatabaseProperties properties;

    public AbstractDatabase(final @NotNull DatabaseProperties properties) {
        this.properties = properties;
    }

    public abstract void connect();
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
    public void createDefaultTable() {
        try (Connection connection1 = getConnection(); PreparedStatement preparedStatement = connection1.prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
