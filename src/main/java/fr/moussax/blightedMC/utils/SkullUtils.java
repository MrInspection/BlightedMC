package fr.moussax.blightedMC.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for handling player skulls in Bukkit.
 *
 * <p>Provides methods to set a skull's owner or apply a
 * Base64-encoded custom texture to {@link SkullMeta}.</p>
 *
 * <p>Stateless and non-instantiable.</p>
 */
public final class SkullUtils {
  private SkullUtils() { }

  public static void applyOwner(SkullMeta meta, UUID uuid) {
    OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
    meta.setOwningPlayer(target);
  }

  public static void applyTexture(SkullMeta meta, String base64Texture) {
    String json = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
    JsonObject object = JsonParser.parseString(json)
      .getAsJsonObject().getAsJsonObject("textures")
      .getAsJsonObject("SKIN");

    String url = object.get("url").getAsString();

    UUID id = UUID.randomUUID();
    PlayerProfile profile = Bukkit.createPlayerProfile(id, id.toString().substring(0, 16));
    PlayerTextures textures = profile.getTextures();

    try {
      textures.setSkin(new URI(url).toURL());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid texture URL: " + url, e);
    }

    profile.setTextures(textures);
    meta.setOwnerProfile(profile);
  }
}
