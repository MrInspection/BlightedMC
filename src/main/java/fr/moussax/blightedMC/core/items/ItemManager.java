package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityExecutor;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.items.rules.ItemRule;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static fr.moussax.blightedMC.core.items.ItemsRegistry.BLIGHTED_ITEMS;
import static fr.moussax.blightedMC.core.items.ItemsRegistry.ID_KEY;

public class ItemManager extends ItemBuilder implements ItemRule {
  private final String itemId;
  private final ItemRarity itemRarity;
  private final ItemType itemType;
  private FullSetBonus fullSetBonus;

  private final List<Ability> abilities = new ArrayList<>();
  private final List<ItemRule> rules = new ArrayList<>();

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material) {
    super(material);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Nonnull String displayName) {
    super(material, rarity.getColorPrefix() + displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount) {
    super(material, amount);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount, @Nonnull String displayName) {
    super(material, amount, rarity.getColorPrefix() + displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, ItemStack itemStack) {
    super(itemStack);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager addAbility(Ability ability) {
    abilities.add(ability);
    return this;
  }

  public ItemManager addRule(ItemRule rule) {
    rules.add(rule);
    return this;
  }

  public List<Ability> getAbilities() {
    return abilities;
  }

  @Override
  public ItemManager setDisplayName(@Nonnull String displayName) {
    String coloredName = itemRarity.getColorPrefix() + displayName;
    super.setDisplayName(coloredName);
    return this;
  }

  public ItemManager setFullSetBonus(FullSetBonus bonus) {
    this.fullSetBonus = bonus;
    return this;
  }

  public FullSetBonus getFullSetBonus() {
    return fullSetBonus;
  }

  public static ItemManager fromItemStack(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType().isAir()) return null;

    var meta = itemStack.getItemMeta();
    if (meta == null) return null;

    var container = meta.getPersistentDataContainer();
    String itemId = container.get(ID_KEY, PersistentDataType.STRING);
    if (itemId == null) return null;

    return BLIGHTED_ITEMS.get(itemId);
  }

  public void triggerAbilities(BlightedPlayer blightedPlayer, Event event) {
    for (Ability ability : abilities) {
      // Check if the ability type matches the current event
      if (shouldTriggerAbility(ability, event)) {
        AbilityExecutor.execute(ability, blightedPlayer, event);
      }
    }

    for (FullSetBonus activeBonus : blightedPlayer.getActiveFullSetBonuses()) {
      if (activeBonus.getPieces() >= activeBonus.getMaxPieces()) {
        activeBonus.startAbility();
      }
    }
  }

  private boolean shouldTriggerAbility(Ability ability, Event event) {
    if (event instanceof org.bukkit.event.player.PlayerInteractEvent interactEvent) {
      org.bukkit.event.block.Action action = interactEvent.getAction();
      AbilityType abilityType = ability.getType();
      
      // Check for sneak requirements first
      if (abilityType.isSneak() && !interactEvent.getPlayer().isSneaking()) {
        return false;
      }
      
      // Check if the ability type matches the current action
      if (abilityType.isRightClick() && (action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || 
                                        action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
        return true;
      }
      if (abilityType.isLeftClick() && (action == org.bukkit.event.block.Action.LEFT_CLICK_AIR || 
                                       action == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)) {
        return true;
      }
      
      return false;
    }
    
    // For other event types, allow all abilities for now
    return true;
  }

  @Override
  public ItemStack toItemStack() {
    ItemStack item = super.toItemStack();

    var meta = item.getItemMeta();
    assert meta != null;
    meta.getPersistentDataContainer().set(
        new NamespacedKey(BlightedMC.getInstance(), "id"),
        PersistentDataType.STRING,
        itemId
    );

    meta.getPersistentDataContainer().set(
        new NamespacedKey(BlightedMC.getInstance(), "rarity"),
        PersistentDataType.STRING,
        itemRarity.name()
    );

    item.setItemMeta(meta);
    return item;
  }

  @Override
  public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
    for (ItemRule rule : rules) {
      if (!rule.canPlace(event, itemStack)) return false;
    }
    return true;
  }

  @Override
  public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
    for (ItemRule rule : rules) {
      if (!rule.canInteract(event, itemStack)) return false;
    }
    return true;
  }

  @Override
  public boolean canUse(Event event, ItemStack itemStack) {
    for (ItemRule rule : rules) {
      if (!rule.canUse(event, itemStack)) return false;
    }
    return true;
  }

  public String getItemId() {
    return itemId;
  }

  public ItemRarity getItemRarity() {
    return itemRarity;
  }

  public ItemType getItemType() {
    return itemType;
  }
}
