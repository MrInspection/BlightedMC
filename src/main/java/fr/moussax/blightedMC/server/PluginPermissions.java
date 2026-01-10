package fr.moussax.blightedMC.server;

public enum PluginPermissions {
    ADMIN("blightedmc.admin"),
    MODERATOR("blightedmc.moderator");

    private final String permission;

    PluginPermissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
