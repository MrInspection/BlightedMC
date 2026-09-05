package fr.moussax.blightedMod.moderator;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ModerationGlowHelper {

    private static final String GLOW_TEAM_NAME = "mod_glow_pink";
    private static final Map<UUID, Scoreboard> ORIGINAL_SCOREBOARDS = new ConcurrentHashMap<>();

    private ModerationGlowHelper() {
    }

    public static void applyPinkGlow(Player moderator, Player target) {
        if (moderator == null || target == null || !moderator.isOnline() || !target.isOnline()) {
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }

        Scoreboard scoreboard = moderator.getScoreboard();
        if (scoreboard.equals(manager.getMainScoreboard())) {
            ORIGINAL_SCOREBOARDS.putIfAbsent(moderator.getUniqueId(), scoreboard);
            scoreboard = manager.getNewScoreboard();
            moderator.setScoreboard(scoreboard);
        }

        Team team = scoreboard.getTeam(GLOW_TEAM_NAME);
        if (team == null) {
            team = scoreboard.registerNewTeam(GLOW_TEAM_NAME);
            team.setColor(ChatColor.LIGHT_PURPLE);
        }

        if (!team.hasEntry(target.getName())) {
            team.addEntry(target.getName());
        }

        sendGlowPacket(moderator, target, true);
    }

    public static void removePinkGlow(Player moderator, Player target) {
        if (moderator == null || target == null || !moderator.isOnline()) {
            return;
        }

        Scoreboard scoreboard = moderator.getScoreboard();
        Team team = scoreboard.getTeam(GLOW_TEAM_NAME);
        if (team != null && team.hasEntry(target.getName())) {
            team.removeEntry(target.getName());
        }

        if (target.isOnline()) {
            sendGlowPacket(moderator, target, false);
        }

        if (team == null || team.getEntries().isEmpty()) {
            Scoreboard originalScoreboard = ORIGINAL_SCOREBOARDS.remove(moderator.getUniqueId());
            if (originalScoreboard != null) {
                moderator.setScoreboard(originalScoreboard);
            }
        }
    }

    private static void sendGlowPacket(Player moderator, Player target, boolean glowing) {
        try {
            CraftPlayer craftTarget = (CraftPlayer) target;
            CraftPlayer craftModerator = (CraftPlayer) moderator;

            byte existingFlags = craftTarget.getHandle().getEntityData().get(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE));
            byte updatedFlags = glowing ? (byte) (existingFlags | 0x40) : (byte) (existingFlags & ~0x40);

            List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
            dataValues.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), updatedFlags));

            ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(target.getEntityId(), dataValues);
            craftModerator.getHandle().connection.send(packet);
        } catch (Throwable _) {
        }
    }
}
