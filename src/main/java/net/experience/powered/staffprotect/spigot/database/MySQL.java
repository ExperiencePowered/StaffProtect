package net.experience.powered.staffprotect.spigot.database;

import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

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
        } catch (SQLSyntaxErrorException e) {
            Bukkit.getPluginManager().disablePlugin(StaffProtectPlugin.getPlugin(StaffProtectPlugin.class));
            throw new RuntimeException("Could not start MySQL, try choosing another MySQL host.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
