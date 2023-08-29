package net.experience.powered.staffprotect.records;

import net.experience.powered.staffprotect.StaffProtect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordFile {

    private static RecordFile instance;

    public static final File folder;

    private final File file;
    private final List<Record> records;

    static {
        folder = new File(StaffProtect.getInstance().getPlugin().getDataFolder(), "records");
        if (!folder.exists()) {
            boolean result = folder.mkdir();
            if (!result) {
                throw new RuntimeException("Could not make new folder : " + folder);
            }
        }
    }

    public RecordFile(final @NotNull File file) {
        this.file = file;
        this.records = new ArrayList<>();

        RecordFile.instance = this;
    }

    public void writeRecord(final @NotNull Record record) {
        record.write(file);
        records.add(record);
    }

    public List<Record> readRecords(final @NotNull String player) {
        List<Record> list = new ArrayList<>();
        records.forEach(record -> {
            if (record.getPlayer().equalsIgnoreCase(player)) {
                list.add(record);
            }
        });
        return list;
    }

    public static RecordFile getInstance() {
        return instance;
    }
}
