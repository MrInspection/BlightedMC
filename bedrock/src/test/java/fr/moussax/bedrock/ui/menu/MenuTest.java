package fr.moussax.bedrock.ui.menu;

import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    @Test
    @DisplayName("Expects getSlot to correctly compute 2D row/column slot index")
    void testGetSlot() {
        assertEquals(0, Menu.getSlot(0, 0));
        assertEquals(8, Menu.getSlot(0, 8));
        assertEquals(9, Menu.getSlot(1, 0));
        assertEquals(49, Menu.getSlot(5, 4));
        assertEquals(53, Menu.getSlot(5, 8));
    }

    @Test
    @DisplayName("Expects MenuSlot to route specific left and right click actions before falling back to ANY_CLICK")
    void testMenuSlotDispatch() {
        AtomicBoolean leftClicked = new AtomicBoolean(false);
        AtomicBoolean rightClicked = new AtomicBoolean(false);
        AtomicBoolean anyClicked = new AtomicBoolean(false);

        ItemStack item = new ItemStack(Material.STONE);
        Menu.MenuSlot slot = new Menu.MenuSlot(item, MenuItemInteraction.LEFT_CLICK, (_, _) -> leftClicked.set(true));
        slot.addAction(MenuItemInteraction.RIGHT_CLICK, (_, _) -> rightClicked.set(true));

        slot.handle(null, ClickType.LEFT);
        assertTrue(leftClicked.get());
        assertFalse(rightClicked.get());

        leftClicked.set(false);
        slot.handle(null, ClickType.RIGHT);
        assertFalse(leftClicked.get());
        assertTrue(rightClicked.get());

        Menu.MenuSlot fallbackSlot = new Menu.MenuSlot(item, MenuItemInteraction.ANY_CLICK, (_, _) -> anyClicked.set(true));
        fallbackSlot.handle(null, ClickType.MIDDLE);
        assertTrue(anyClicked.get());
    }

    @Test
    @DisplayName("Expects static MenuSlot to have no actions and muted sound")
    void testStaticSlotBehavior() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Menu.MenuSlot staticSlot = new Menu.MenuSlot(item);

        assertFalse(staticSlot.hasAction());
        assertFalse(staticSlot.hasAction(ClickType.LEFT));
        assertFalse(staticSlot.hasAction(ClickType.RIGHT));
        assertTrue(staticSlot.isSoundMuted());

        // Handling a click on an actionless slot must be a no-op
        assertDoesNotThrow(() -> staticSlot.handle(null, ClickType.LEFT));
    }

    @Test
    @DisplayName("Expects interactive MenuSlot to have default click sound and allow custom/muted sound")
    void testInteractiveSlotSoundCustomization() {
        ItemStack item = new ItemStack(Material.COMPASS);
        Menu.MenuSlot slot = new Menu.MenuSlot(item, MenuItemInteraction.ANY_CLICK, (_, _) -> {});

        assertTrue(slot.hasAction());
        assertTrue(slot.hasAction(ClickType.LEFT));
        assertFalse(slot.isSoundMuted());
        assertTrue(slot.isUsingDefaultClickSound());
        assertNull(slot.getSound());

        // Custom page turn sound
        slot.withPageTurnSound();
        assertFalse(slot.isSoundMuted());
        assertFalse(slot.isUsingDefaultClickSound());
        assertTrue(slot.isUsingPageTurnSound());
        assertNull(slot.getSound());

        // Muted sound
        slot.withoutSound();
        assertTrue(slot.isSoundMuted());
        assertFalse(slot.isUsingDefaultClickSound());
        assertFalse(slot.isUsingPageTurnSound());

        // Custom sound configuration with volume and pitch
        slot.withSound(null, 0.8f, 0.5f);
        assertNull(slot.getSound());
        assertEquals(0.8f, slot.getVolume());
        assertEquals(0.5f, slot.getPitch());
        assertTrue(slot.isSoundMuted());
        assertFalse(slot.isUsingDefaultClickSound());
        assertFalse(slot.isUsingPageTurnSound());
    }

    @Test
    @DisplayName("Expects standard framed grid and border slots to match 54-slot inventory geometry")
    void testFramedSlotGeometry() {
        assertEquals(28, PaginatedMenu.INNER_GRID_SLOTS.length);
        assertEquals(23, PaginatedMenu.FRAME_SLOTS.length);

        // Verify no overlap between inner grid and frame slots
        for (int inner : PaginatedMenu.INNER_GRID_SLOTS) {
            for (int frame : PaginatedMenu.FRAME_SLOTS) {
                assertNotEquals(inner, frame, "Inner slot " + inner + " cannot overlap frame slot " + frame);
            }
        }

        // Verify all frame and inner slots are within standard 54-slot bounds (0..53)
        for (int inner : PaginatedMenu.INNER_GRID_SLOTS) {
            assertTrue(inner >= 0 && inner < 54);
        }
        for (int frame : PaginatedMenu.FRAME_SLOTS) {
            assertTrue(frame >= 0 && frame < 54);
        }
    }
}


