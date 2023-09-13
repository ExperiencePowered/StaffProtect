package net.experience.powered.staffprotect.records;

import net.experience.powered.staffprotect.StaffProtect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RecordFile {

    private static RecordFile instance;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public static final File folder;

    private final File file;

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
        RecordFile.instance = this;
    }

    public void writeRecord(final @NotNull Record record) {
        record.write(file);
    }

    public CompletableFuture<List<CapturedRecordFile>> readRecords(final @NotNull Player player) {
        final Pattern pattern = Pattern.compile("\\[(.*?)\\]\\:");
        return CompletableFuture.supplyAsync(() -> {
            final List<CapturedRecordFile> captures = new ArrayList<>();
            final File[] files = folder.listFiles();
            if (files == null) {
                return captures;
            }
            for(File file1 : files) {
                if (file1.isDirectory()) {
                    break;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(file1))) {
                    final CapturedRecordFile capture = new CapturedRecordFile(file1);
                    Stream<String> stream = reader.lines();
                    stream.forEach(line -> {
                        long time;
                        String content;
                        String[] stringArray = line.split(" ");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            content = line.substring(matcher.start() + stringArray[1].length() + 1);
                        }
                        else {
                            content = null;
                        }

                        String array1 = stringArray[0];
                        if (array1.startsWith("(") && array1.endsWith(")")) {
                            array1 = array1.replace("(", "")
                                    .replace(")", "");
                            try {
                                Date date = dateFormat.parse(array1);
                                time = date.getTime();
                            } catch (NumberFormatException | ParseException e) {
                                time = -1;
                            }
                        }
                        else {
                            time = -1;
                        }
                        
                        String array2 = stringArray[1];
                        if (array2.startsWith("[") && array2.endsWith("]:")) {
                            array2 = array2.replace("[", "")
                                    .replace("]:", "");
                            if (player.getName().equalsIgnoreCase(array2)) {
                                capture.getRecords().add(new Record(time, player.getName(), content));
                            }
                            InetSocketAddress address = player.getAddress();
                            if (address != null & (address.getHostString() + ":" + address.getPort()).equals(array2)) {
                                capture.getRecords().add(new Record(time, player.getName(), content));
                            }
                        }
                    });
                    captures.add(capture);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return captures;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return List.of();
        });
    }

    public static RecordFile getInstance() {
        return instance;
    }
}
