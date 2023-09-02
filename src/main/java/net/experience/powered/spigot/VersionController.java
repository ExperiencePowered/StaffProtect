package net.experience.powered.spigot;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Properties;

public class VersionController {

    private final File folder;
    private final String gitLastTag;
    private final String gitHash;
    private final String gitHashFull;
    private final String gitBranchName;
    private final String gitIsCleanTag;
    private final String version;

    public VersionController(final @NotNull File folder) {
        this.folder = folder;

        Properties versionProperties = new Properties();
        InputStream stream = null;

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("version.properties");
            if(inputStream == null) {
                try {
                    stream = new FileInputStream("bin/main/version.properties");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                stream = inputStream;
            }
            versionProperties.load(stream);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Could not load classpath:/version.properties", e);
        }

        gitLastTag = versionProperties.getProperty("gitLastTag","last-tag-not-found");
        gitHash = versionProperties.getProperty("gitHash","git-hash-not-found");
        gitHashFull = versionProperties.getProperty("gitHashFull", "git-hash-full-not-found");
        gitBranchName = versionProperties.getProperty("gitBranchName","git-branch-name-not-found");
        gitIsCleanTag = versionProperties.getProperty("gitIsCleanTag","git-isCleanTag-not-found");
        version = versionProperties.getProperty("version", "version-not-found");

        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGitBranchName() {
        return gitBranchName;
    }

    public String getGitHash() {
        return gitHash;
    }

    public String getGitHashFull() {
        return gitHashFull;
    }

    public String getGitIsCleanTag() {
        return gitIsCleanTag;
    }

    public String getGitLastTag() {
        return gitLastTag;
    }

    public String getVersion() {
        return version;
    }
}