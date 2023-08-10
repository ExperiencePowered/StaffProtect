package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.interfaces.Permission;
import net.experience.powered.staffprotect.notification.NotificationBus;
import org.jetbrains.annotations.NotNull;

public class StaffProtectAPIImpl implements StaffProtectAPI {

    private final Permission permission;
    private final NotificationBus bus;

    public StaffProtectAPIImpl(final @NotNull Permission permission, final @NotNull NotificationBus bus) {
        this.permission = permission;
        this.bus = bus;
    }


    @Override
    public @NotNull Permission getPermission() {
        return permission;
    }

    @Override
    public @NotNull NotificationBus getNotificationBus() {
        return bus;
    }
}
