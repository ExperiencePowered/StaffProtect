package net.experience.powered.staffprotect.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + this.file);
        dataSource = new HikariDataSource(config);

        try {
            PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS verification (playerName varchar(255), secretKey varchar(31))");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
