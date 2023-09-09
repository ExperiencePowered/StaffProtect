package net.experience.powered.staffprotect.spigot.database;

import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MySQL extends AbstractDatabase {
    public MySQL(final @NotNull DatabaseProperties properties) {
        super(properties);
    }

    @Override
    public void connect() {
        properties.initializeNullRunnable((string) -> {
            throw new IllegalStateException("Property " + string + " is null.");
        });

        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String builder = "jdbc:mysql://" + properties.getProperty("host") +
                ":" +
                properties.getProperty("port") +
                "/" +
                properties.getProperty("database") +
                "?useSSL=" +
                properties.getProperty("useSSL");
        try {
            connection = DriverManager.getConnection(builder, (String) properties.getProperty("username"), (String) properties.getProperty("password"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))")){
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        disconnect();
    }
}
