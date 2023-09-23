package net.experience.powered.staffprotect.records;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.database.AbstractDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class RecordFile {

    private static RecordFile instance;

    public static AbstractDatabase databaseInstance;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-LL-yy");
    public static final Pattern pattern = Pattern.compile("([0-9]{2}-){2}[0-9]{2}");
    public static File file;

    static {
        file = new File(StaffProtect.getInstance().getPlugin().getDataFolder(), "Records.db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not make new file.", e);
            }
        }
    }

    public abstract void writeRecord(final @NotNull Record record);
    public abstract CompletableFuture<List<Record>> readRecords(final @Nullable String user, final @Nullable Integer limit, final @Nullable ActionType action, final @Nullable String date);

    public static void setInstance(RecordFile instance) {
        if (RecordFile.instance == null) {
            RecordFile.instance = instance;
        }
    }

    public static RecordFile getInstance() {
        return instance;
    }
}
