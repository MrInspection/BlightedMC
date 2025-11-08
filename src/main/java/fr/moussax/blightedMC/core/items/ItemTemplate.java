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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static fr.moussax.blightedMC.core.items.registry.ItemsRegistry.REGISTERED_ITEMS;
import static fr.moussax.blightedMC.core.items.registry.ItemsRegistry.ID_KEY;

/**
 * A class for creating custom items in BlightedMC, extending {@link ItemBuilder}.
 * <p>
 * Allows adding item ID, rarity, type, abilities, rules, and full set bonuses.
 * Provides methods to generate {@link ItemStack} instances with persistent data and
 * handle ability triggering based on events.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ItemTemplate blazingSword = new ItemTemplate("blazing_sword", ItemType.SWORD, ItemRarity.LEGENDARY, Material.DIAMOND_SWORD)
 *      .setDisplayName("§cBlazing Sword")
 *      .addAbility(new FireSlashAbility())
 *      .addRule(new CannotPlaceOnGrassRule());
 *      .addToRegistry();
 * }</pre>
 */
public class ItemTemplate extends ItemBuilder implements ItemRule, ItemGenerator {
  public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "id");
  public static final NamespacedKey ITEM_RARITY_KEY = new NamespacedKey(BlightedMC.getInstance(), "rarity");

  private final String itemId;
  private final ItemRarity itemRarity;
  private final ItemType itemType;
  private FullSetBonus fullSetBonus;

  private final List<Ability> abilities = new ArrayList<>();
  private final List<ItemRule> rules = new ArrayList<>();

  public ItemTemplate(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material) {
    super(material);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemTemplate(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Nonnull String displayName) {
    super(material, rarity.getColorPrefix() + displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemTemplate(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount) {
    super(material, amount);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemTemplate(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount, @Nonnull String displayName) {
    super(material, amount, rarity.getColorPrefix() + displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemTemplate(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, ItemStack itemStack) {
    super(itemStack);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public void addAbility(Ability ability) {
    abilities.add(ability);
  }

  public void addRule(ItemRule rule) {
    rules.add(rule);
  }

  public List<Ability> getAbilities() {
    return abilities;
  }

  @Override
  public ItemTemplate setDisplayName(@Nonnull String displayName) {
    String coloredName = itemRarity.getColorPrefix() + displayName;
    super.setDisplayName(coloredName);
    return this;
  }

  public void setFullSetBonus(FullSetBonus bonus) {
    this.fullSetBonus = bonus;
  }

  public FullSetBonus getFullSetBonus() {
    return fullSetBonus;
  }

  public static ItemTemplate fromItemStack(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType().isAir()) return null;

    var meta = itemStack.getItemMeta();
    if (meta == null) return null;

    var container = meta.getPersistentDataContainer();
    String itemId = container.get(ID_KEY, PersistentDataType.STRING);
    if (itemId == null) return null;

    return REGISTERED_ITEMS.get(itemId);
  }

  public void triggerAbilities(BlightedPlayer blightedPlayer, Event event) {
    for (Ability ability : abilities) {
      if (shouldTriggerAbility(ability, event)) {
        AbilityExecutor.execute(ability, blightedPlayer, event);
      }
    }

    for (FullSetBonus activeBonus : blightedPlayer.getActiveFullSetBonuses()) {
      if (activeBonus.getPieces() >= activeBonus.getMaxPieces()) {
        activeBonus.activate();
      }
    }
  }

  private boolean shouldTriggerAbility(Ability ability, Event event) {
    if (event instanceof PlayerInteractEvent interactEvent) {
      Action action = interactEvent.getAction();
      AbilityType abilityType = ability.type();

      if (abilityType.isSneak() && !interactEvent.getPlayer().isSneaking()) {
        return false;
      }

      boolean rightClick = abilityType.isRightClick() &&
        (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

      boolean leftClick = abilityType.isLeftClick() &&
        (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);

      return rightClick || leftClick;
    }
    return true;
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

  @Override
  public ItemStack toItemStack() {
    ItemStack item = super.toItemStack();

    var meta = item.getItemMeta();
    assert meta != null;
    meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
    meta.getPersistentDataContainer().set(ITEM_RARITY_KEY, PersistentDataType.STRING, itemRarity.name());
    item.setItemMeta(meta);
    return item;
  }

  @Override
  public ItemStack createItemStack() {
    return this.toItemStack();
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
