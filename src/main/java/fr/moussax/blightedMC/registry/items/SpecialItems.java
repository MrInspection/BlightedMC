package fr.moussax.blightedMC.registry.items;

import fr.moussax.blightedMC.core.items.*;
import org.bukkit.Material;

public class SpecialItems implements ItemCategory {
  @Override
  public void registerItems() {
    ItemManager blightedGemstone = new ItemManager("BLIGHTED_GEMSTONE", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.PLAYER_HEAD, "Blighted Gemstone");
    blightedGemstone.addLore(
      "§7A gemstone tainted by §5shadow§7, tore",
      "§7from the §5remains §7of a fallen horror. ",
      "§7Its core pulses with sealed §dBlighted",
      "§dFavors §7awaiting one bold enough to",
      "§7claim them.",
      "",
      "§7Favors trapped: §d???✵",
      "",
      "§dRight click to consume!",
      "",
      ItemRarity.SPECIAL.getName()
    );
  }
}
