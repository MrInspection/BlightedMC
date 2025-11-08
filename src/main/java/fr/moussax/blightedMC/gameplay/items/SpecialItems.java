package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.entities.loot.favors.FavorsItem;
import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import org.bukkit.Material;

import java.util.List;

public class SpecialItems implements ItemGroup {
  @Override
  public List<ItemTemplate> registerItems() {
    ItemTemplate blightedGemstone = new ItemTemplate("BLIGHTED_GEMSTONE", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.PLAYER_HEAD, "Blighted Gemstone");
    blightedGemstone.addLore(
      "§7A gemstone tainted by §5shadow§7, tore",
      "§7from the §5remains §7of a fallen horror. ",
      "§7Its core pulses with sealed §dBlighted",
      "§dFavors §7awaiting one bold enough to",
      "§7claim them.",
      "",
      "§7Favors trapped: §6???✵",
      "",
      "§dRight click to consume!",
      "",
      ItemRarity.SPECIAL.getName()
    );

    blightedGemstone.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2MjM1MjFjODExMWFkMjllOWRjZjdhY2M1NjA4NWE5YWIwN2RhNzMyZDE1MTg5NzZhZWU2MWQwYjNlM2JkNiJ9fX0=");
    blightedGemstone.addRule(new PreventPlacementRule());
    blightedGemstone.addAbility(new Ability(new FavorsItem.BlightedGemstoneAbility(), "Consume Favors", AbilityType.RIGHT_CLICK));
    return List.of(blightedGemstone);
  }
}
