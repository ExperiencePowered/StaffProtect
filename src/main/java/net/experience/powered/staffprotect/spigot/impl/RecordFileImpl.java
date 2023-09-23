package net.experience.powered.staffprotect.spigot.impl;

import net.experience.powered.staffprotect.database.DatabaseProperties;
import net.experience.powered.staffprotect.records.ActionType;
import net.experience.powered.staffprotect.records.Record;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.database.SQLite;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RecordFileImpl extends RecordFile {

    private final String table;

    public RecordFileImpl() {
        final DatabaseProperties properties = new DatabaseProperties(file.getName());
        properties.write("file", file);
        RecordFile.databaseInstance = new SQLite(properties);
        Instant instant = Instant.now(Clock.system(StaffProtectPlugin.zoneId));
        table = RecordFile.dateFormat.format(Date.from(instant));
        try (Connection connection = databaseInstance.getConnection();
             PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS '" + table + "' (time int(255), player varchar(255), action varchar(255), message longtext);")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        RecordFile.setInstance(this);
    }

    @Override
    public void writeRecord(final @NotNull Record record) {
        CompletableFuture.runAsync(() -> {
            Connection connection = databaseInstance.getConnection();
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO '" + table + "' VALUES (?,?,?,?);")) {
                ps.setLong(1, record.getTime());
                ps.setString(2, record.getPlayer());
                ps.setString(3, record.getAction().toString());
                ps.setString(4, record.getContent());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        });
    }

    @Override
    public CompletableFuture<List<Record>> readRecords(final @Nullable String user, final @Nullable Integer limit, final @Nullable ActionType action, final @Nullable String date) {
        return CompletableFuture.supplyAsync(() -> {
            final List<Record> list = new ArrayList<>();

            @NotNull String finalUser = user == null ? "" : user;
            int finalLimit = limit == null ? -1 : limit;

            String playerCommand = finalUser.equalsIgnoreCase("") ? "" : "WHERE player = '" + finalUser + "'";
            String actionCommand = action == null ? "" : playerCommand.equalsIgnoreCase("") ? "WHERE action = '" + action + "'" : "AND action = '" + action + "'";

            int i = 0;
            Connection connection = databaseInstance.getConnection();
            DatabaseMetaData metaData;

            try {
                metaData = connection.getMetaData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (ResultSet tables = metaData.getTables(null, null, "%", null)) {
                while ((finalLimit == -1 || i < finalLimit) && tables.next()) {
                    String tableName = tables.getString(3);

                    if (tableName.equalsIgnoreCase("sqlite_schema")) {
                        continue;
                    }

                    if (date != null && !date.equalsIgnoreCase(tableName)) {
                        continue;
                    }

                    String statement = "SELECT * FROM '" + tableName + "' " + playerCommand + actionCommand + ";";
                    try (PreparedStatement ps = connection.prepareStatement(statement); ResultSet resultSet = ps.executeQuery()) {
                        while ((finalLimit == -1 || i < finalLimit) && resultSet.next()) {
                            i++;
                            long time = resultSet.getLong("time");
                            String player = resultSet.getString("player");
                            ActionType type = ActionType.valueOf(resultSet.getString("action"));
                            String message = resultSet.getString("message");
                            list.add(new Record(time, player, type, message));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return list;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        });
    }
}
