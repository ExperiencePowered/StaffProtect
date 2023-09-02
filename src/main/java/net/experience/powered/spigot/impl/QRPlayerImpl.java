package net.experience.powered.spigot.impl;

import net.experience.powered.staffprotect.verifications.qr.QRPlayer;
import org.jetbrains.annotations.NotNull;

public class QRPlayerImpl implements QRPlayer {

    private final boolean firstVerify;
    private final String secretKey;
    private String lastCode;
    private String code;

    public QRPlayerImpl(final @NotNull String secretKey, final boolean firstVerify) {
        this.secretKey = secretKey;
        this.firstVerify = firstVerify;
    }

    @Override
    public boolean isFirstVerify() {
        return firstVerify;
    }

    @Override
    public @NotNull String getSecretKey() {
        return secretKey;
    }

    public String getCode() {
        return code;
    }

    public String getLastCode() {
        return lastCode;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLastCode(String lastCode) {
        this.lastCode = lastCode;
    }
}
