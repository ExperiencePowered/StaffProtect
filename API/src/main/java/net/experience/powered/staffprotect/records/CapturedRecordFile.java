package net.experience.powered.staffprotect.records;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class CapturedRecordFile {

    private boolean corrupted = false;

    private FileTime creationTime = null;
    private final String fileName;
    private final List<Record> records;

    public CapturedRecordFile(final @NotNull File file) {
        this.records = new ArrayList<>();
        this.fileName = file.getName();

        try {
            URI uri = new URI(file.getPath());
            BasicFileAttributes attr = Files.readAttributes(Path.of(uri), BasicFileAttributes.class);
            this.creationTime = attr.creationTime();
        } catch (IOException | URISyntaxException e) {
            this.corrupted = true;
            e.printStackTrace();
        }
    }
    public boolean isCorrupted() {
        return corrupted;
    }

    public FileTime getCreationTime() {
        return creationTime;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Record> getRecords() {
        return records;
    }
}
