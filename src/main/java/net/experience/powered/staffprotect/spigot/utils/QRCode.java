package net.experience.powered.staffprotect.spigot.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import net.experience.powered.staffprotect.spigot.impl.QRPlayerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class QRCode {

    private final static HashMap<UUID, QRPlayerImpl> codes = new HashMap<>();
    private final static HashMap<UUID, ItemStack> previousItem = new HashMap<>();

    public static HashMap<UUID, QRPlayerImpl> getCodes() {
        return codes;
    }

    public static HashMap<UUID, ItemStack> getPreviousItem() {
        return previousItem;
    }

    public static @NotNull String getBarCode(final @NotNull String secretKey, final @NotNull String account, final @NotNull String issuer) {
        return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
                + "?secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8).replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static @NotNull BufferedImage getImage(final @NotNull String barCode) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, 128, 128);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull ItemStack toMap(final @NotNull Player player, final @NotNull QRPlayerImpl qrPlayer, final @NotNull String issuer) {
        final ItemStack map = new ItemStack(Material.FILLED_MAP);
        final MapMeta mapMeta = (MapMeta) map.getItemMeta();
        assert mapMeta != null;
        MapView mapView;
        if (!mapMeta.hasMapView()) {
            mapView = Bukkit.getServer().createMap(player.getWorld());
        }
        else {
            mapView = mapMeta.getMapView();
        }
        assert mapView != null;
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                canvas.drawImage(0, 0, QRCode.getImage(QRCode.getBarCode(qrPlayer.getSecretKey(), player.getName(), issuer)));
            }
        });
        mapMeta.setMapView(mapView);
        map.setItemMeta(mapMeta);
        return map;
    }
}
