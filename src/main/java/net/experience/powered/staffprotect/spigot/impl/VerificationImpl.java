package net.experience.powered.staffprotect.spigot.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.experience.powered.staffprotect.global.EncodingUtil;
import net.experience.powered.staffprotect.records.Record;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.database.AbstractDatabase;
import net.experience.powered.staffprotect.spigot.messages.PluginMessageManager;
import net.experience.powered.staffprotect.spigot.utils.Expiring;
import net.experience.powered.staffprotect.spigot.utils.QRCode;
import net.experience.powered.staffprotect.verifications.Verification;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VerificationImpl extends Verification {

    private static int SLOT;
    private static String ISSUER;

    private final StaffProtectPlugin plugin;
    private final AbstractDatabase database;

    public VerificationImpl() {
        this.plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
        this.database = plugin.getDatabase();

        VerificationImpl.SLOT = plugin.getConfig().getInt("staff-verification.qr-code.slot", 4);
        VerificationImpl.ISSUER = "Minecraft Verification " + plugin.getConfig().getString("staff-verification.qr-code.server-name", "ExampleServer");
    }

    @Override
    public void start(final @NotNull Player player) {
        CompletableFuture.supplyAsync(() -> {
            QRPlayerImpl qrPlayer;
            Connection connection = database.getConnection();
            EncodingUtil util = EncodingUtil.getInstance();
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT secretKey FROM verification WHERE playerName = (?) LIMIT 1;")){
                preparedStatement.setString(1, player.getName());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String secretKey = resultSet.getString("secretKey");
                    qrPlayer = new QRPlayerImpl(util.decode(secretKey.getBytes(StandardCharsets.UTF_8)), false);
                }
                else {
                    final GoogleAuthenticator gAuth = new GoogleAuthenticator();
                    final GoogleAuthenticatorKey key = gAuth.createCredentials();
                    qrPlayer = new QRPlayerImpl(key.getKey(), true);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (qrPlayer.isFirstVerify()) {
                try (PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO verification (playerName, secretKey) VALUES (?, ?);")){
                    preparedStatement1.setString(1, player.getName());
                    preparedStatement1.setString(2, new String(util.encode(qrPlayer.getSecretKey()), StandardCharsets.UTF_8));
                    preparedStatement1.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return qrPlayer;
        }).thenAccept((qrPlayer) -> {
            if (qrPlayer.isFirstVerify()) {
                final PlayerInventory inventory = player.getInventory();
                final ItemStack previousItem = inventory.getItem(SLOT);
                if (previousItem != null) {
                    QRCode.getPreviousItem().put(player.getUniqueId(), previousItem);
                }
                inventory.setItem(SLOT, QRCode.toMap(player, qrPlayer, ISSUER));
                inventory.setHeldItemSlot(SLOT);

                final String fallback = "<red>Hello <blue><player></blue>, since you are Staff, please scan this QR code and save it into your Authenticator app, if you have issues with loading QR code, you can <green><hover:show_text:'<gold>Click to copy secret key!'><click:COPY_TO_CLIPBOARD:'<secretkey>'>reveal secret key</click></hover></green>.";
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.first-verify", fallback),
                                Placeholder.parsed("player", player.getName()),
                                Placeholder.parsed("secretkey", qrPlayer.getSecretKey())));
            }
            else {
                final String fallback = "<red>Hello <blue><player></blue>, seems like you are Staff, please verify with your code from Authenticator app.";
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.prompt-to-verify", fallback),
                        Placeholder.parsed("player", player.getName())));
            }
            new Expiring(player, this);
            QRCode.getCodes().put(player.getUniqueId(), qrPlayer);
        }).exceptionally(throwable -> {
            Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer("We were unable to verify you!"));
            throwable.printStackTrace();
            return null;
        });
    }

    @Override
    public void end(final @NotNull Player player) {
        if (QRCode.getPreviousItem().containsKey(player.getUniqueId())) {
            final ItemStack itemStack = QRCode.getPreviousItem().getOrDefault(player.getUniqueId(), new ItemStack(Material.AIR));
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                player.getInventory().setItem(SLOT, new ItemStack(Material.AIR));
            }
            else {
                player.getInventory().setItem(SLOT, itemStack);
            }
        }
        QRCode.getCodes().remove(player.getUniqueId());
        QRCode.getPreviousItem().remove(player.getUniqueId());
    }

    @Override
    public boolean authorize(final @NotNull Player player, int code) {
        final GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final QRPlayerImpl qrPlayer = QRCode.getCodes().get(player.getUniqueId());
        boolean result = gAuth.authorize(qrPlayer.getSecretKey(), code);
        if (isAuthorized(player)) {
            return true;
        }
        if (result) {
            end(player);
            Optional<PluginMessageManager> optional = plugin.getMessageManager();
            optional.ifPresent(pluginMessageManager -> pluginMessageManager.sendAuthorization(player));
            RecordFile.getInstance().writeRecord(new Record(System.currentTimeMillis(), player.getName(), "Staff was authorized."));
        }
        return result;
    }

    @Override
    public void forceAuthorize(@NotNull Player player) {
        final GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final QRPlayerImpl qrPlayer = QRCode.getCodes().get(player.getUniqueId());
        if (isAuthorized(player)) {
            authorize(player, gAuth.getTotpPassword(qrPlayer.getSecretKey()));
        }
    }

    @Override
    public boolean isAuthorized(final @NotNull Player player) {
        return QRCode.getCodes().get(player.getUniqueId()) == null;
    }

    public static Verification getInstance() {
        return Verification.getInstance();
    }
}
