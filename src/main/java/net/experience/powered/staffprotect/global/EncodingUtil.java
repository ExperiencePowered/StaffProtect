package net.experience.powered.staffprotect.global;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtil {

    private static EncodingUtil instance;

    public byte[] encode(final @NotNull String input) {
        return Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public String decode(final byte @NotNull [] hash) {
        return new String(Base64.getDecoder().decode(hash), StandardCharsets.UTF_8);
    }

    public static EncodingUtil getInstance() {
        if (instance == null) {
            EncodingUtil.instance = new EncodingUtil();
        }
        return instance;
    }
}
