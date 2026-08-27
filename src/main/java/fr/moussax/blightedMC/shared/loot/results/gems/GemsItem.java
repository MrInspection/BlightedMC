package fr.moussax.blightedMC.shared.loot.results.gems;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.content.sound.BlightedSounds;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.abilities.AbilityManager;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.text.Messenger;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Representation of a consumable Blighted Gemstone item carrying a gem quantity.
 *
 * @param amount quantity of gems carried by this gemstone item
 */
public record GemsItem(int amount) implements Supplier<ItemStack> {

    /**
     * Constructs a GemsItem by reading the gem quantity from an existing item stack's persistent data.
     *
     * @param itemStack item stack containing gemstone persistent data
     */
    public GemsItem(ItemStack itemStack) {
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta(), "itemMeta cannot be null");

        Integer value = meta.getPersistentDataContainer().get(
                new NamespacedKey(BlightedMC.getInstance(), "gems"), PersistentDataType.INTEGER);
        this(value != null ? value : 1);
    }

    /**
     * Adds this gemstone's gem quantity to a player's balance.
     *
     * @param player player receiving the gems
     */
    public void addGems(BlightedPlayer player) {
        player.addGems(amount);
    }

    /**
     * Ability handler for consuming Blighted Gemstone items on player interaction.
     */
    public static class BlightedGemstoneAbility implements AbilityManager<PlayerInteractEvent> {

        @Override
        public boolean triggerAbility(PlayerInteractEvent event) {
            if (event.getItem() == null) return false;
            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(event.getPlayer());
            GemsItem gemsItem = new GemsItem(event.getItem());

            if (gemsItem.amount <= 0) {
                Messenger.warn(event.getPlayer(), "This gemstone doesn't have any gems to redeem.");
                return false;
            }

            gemsItem.addGems(blightedPlayer);
            event.getPlayer().sendMessage("§8 ■ §7You received §d" + gemsItem.amount + "✵ Gems §7from a §5Blighted Gemstone.");
            BlightedSounds.BLIGHTED_GEMSTONE_CONSUME.play(event.getPlayer().getLocation());
            event.getPlayer().getInventory().remove(event.getItem());
            event.setCancelled(true);
            return true;
        }

        @Override
        public int getCooldownSeconds() {
            return 0;
        }

        @Override
        public int getManaCost() {
            return 0;
        }

        @Override
        public boolean canTrigger(BlightedPlayer player) {
            return true;
        }

        @Override
        public void start(BlightedPlayer player) {
        }

        @Override
        public void stop(BlightedPlayer player) {
        }
    }

    /**
     * Constructs the {@link ItemStack} for this gemstone with lore and persistent data applied.
     *
     * @return constructed gemstone item stack
     */
    @Override
    public ItemStack get() {
        BlightedItem blightedItem = ItemRegistry.getItem("BLIGHTED_GEMSTONE");

        blightedItem.setLore(6, "§8 Gems: §d" + this.amount + "✵");
        ItemStack itemStack = blightedItem.toItemStack();

        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta(), "itemMeta cannot be null");
        meta.getPersistentDataContainer().set(new NamespacedKey(BlightedMC.getInstance(), "gems"), PersistentDataType.INTEGER, amount);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
