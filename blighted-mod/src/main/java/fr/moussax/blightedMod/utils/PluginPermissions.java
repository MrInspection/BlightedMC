package fr.moussax.blightedMod.utils;

public enum PluginPermissions {
    MODERATOR("blightedmod.moderator"),
    ADMIN("blightedmod.administrator");

    private final String node;

    PluginPermissions(String node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return node;
    }
}
