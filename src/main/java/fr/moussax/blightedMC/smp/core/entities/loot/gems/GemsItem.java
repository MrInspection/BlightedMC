package fr.moussax.blightedMC.smp.core.entities.loot.gems;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.ItemGenerator;
import fr.moussax.blightedMC.smp.core.items.ItemTemplate;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.smp.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public record GemsItem(int amount) implements ItemGenerator {
    public GemsItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        Integer value = meta.getPersistentDataContainer().get(
            new NamespacedKey(BlightedMC.getInstance(), "gems"), PersistentDataType.INTEGER);
        this(value != null ? value : 1);
    }

    public void addGems(BlightedPlayer player) {
        player.addGems(amount);
    }

    public static class BlightedGemstoneAbility implements AbilityManager<PlayerInteractEvent> {

        @Override
        public boolean triggerAbility(PlayerInteractEvent event) {
            if (event.getItem() == null) return false;
            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(event.getPlayer());
            GemsItem gemsItem = new GemsItem(event.getItem());

            if (gemsItem.amount <= 0) {
                Formatter.warn(event.getPlayer(), "This gemstone doesn't have any gems to redeem.");
                return false;
            }

            gemsItem.addGems(blightedPlayer);
            event.getPlayer().sendMessage("§8 ■ §7You received §d" + gemsItem.amount + "✵ Gems §7from a §5Blighted Gemstone.");
            SoundSequence.BLIGHTED_GEMSTONE_CONSUME.play(event.getPlayer().getLocation());
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

    @Override
    public ItemStack createItemStack() {
        ItemTemplate itemTemplate = ItemDirectory.getItem("BLIGHTED_GEMSTONE");

        if (itemTemplate == null) {
            throw new IllegalStateException("BLIGHTED_GEMSTONE is not registered. Ensure ItemDirectory.initializeItems() runs before creating Gems items.");
        }

        itemTemplate.setLore(7, "§8 Gems: §d" + this.amount + "✵");
        ItemStack itemStack = itemTemplate.toItemStack();

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(new NamespacedKey(BlightedMC.getInstance(), "gems"), PersistentDataType.INTEGER, amount);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
