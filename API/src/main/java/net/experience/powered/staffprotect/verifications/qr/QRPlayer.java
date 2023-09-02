package net.experience.powered.staffprotect.verifications.qr;

import org.jetbrains.annotations.NotNull;

public interface QRPlayer {

    boolean isFirstVerify();
    @NotNull String getSecretKey();

}
