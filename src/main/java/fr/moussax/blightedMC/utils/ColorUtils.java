package fr.moussax.blightedMC.utils;

import org.bukkit.Color;

public final class ColorUtils {
  private ColorUtils() {}

  /**
   * Parses a 6-digit hex string into a {@link Color}.
   *
   * @param hex hex string with or without leading "#"
   * @return Bukkit Color
   * @throws IllegalArgumentException if the hex is invalid
   */
  public static Color fromHex(String hex) {
    if (hex == null || !hex.matches("^#?[0-9a-fA-F]{6}$")) {
      throw new IllegalArgumentException("Invalid hex color: " + hex);
    }
    String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
    return Color.fromRGB(Integer.parseInt(cleaned, 16));
  }
}
