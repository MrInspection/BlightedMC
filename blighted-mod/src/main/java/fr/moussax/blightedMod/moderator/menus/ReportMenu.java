package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.bedrock.ui.menu.Menu;
import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.reports.ReportData;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class ReportMenu extends Menu {

    private record ReportCategory(int slot, Material material, String reason, String line1, String line2) {}

    private static final ReportCategory[] CATEGORIES = {
            new ReportCategory(20, Material.WRITABLE_BOOK, "Chat Abuse / Scam",
                    "§7Using offensive chat, scamming,", "§7or engaging in offensive chat behavior."),
            new ReportCategory(21, Material.DIAMOND_SWORD, "Cheating / Hacks",
                    "§7Using illegal modifications, movement", "§7hacks, or combat advantages."),
            new ReportCategory(22, Material.NAME_TAG, "Inappropriate Skin / Name",
                    "§7Using offensive, inappropriate, or", "§7rule-breaking skins or usernames."),
            new ReportCategory(23, Material.CHEST, "Griefing / Theft",
                    "§7Destroying claims, stealing items,", "§7or damaging another player's property."),
            new ReportCategory(24, Material.COMPASS, "Cross-Teaming / Exploits",
                    "§7Teaming across alliances or abusing", "§7server bugs and game exploits."),
            new ReportCategory(29, Material.SCAFFOLDING, "Inappropriate Build",
                    "§7Building offensive structures, hate", "§7symbols, or rule-breaking builds."),
            new ReportCategory(30, Material.CLOCK, "Spam / Chat Flooding",
                    "§7Flooding chat with repeated messages", "§7or excessive unwanted content."),
            new ReportCategory(31, Material.WITHER_ROSE, "Harassment / Toxicity",
                    "§7Targeted harassment, hate speech,", "§7or persistently toxic behavior."),
            new ReportCategory(32, Material.PAPER, "Other",
                    "§7Any rule-breaking behavior not", "§7covered by the categories above."),
    };

    private final String targetName;
    private final String attachedMessage;

    public ReportMenu(String targetName) {
        this(targetName, "General player report");
    }

    public ReportMenu(String targetName, String attachedMessage) {
        super("Report " + targetName, 54);
        this.targetName = targetName;
        this.attachedMessage = attachedMessage != null ? attachedMessage : "General player report";
    }

    @Override
    public void build(@NonNull Player reporter) {
        renderTargetHead();

        for (ReportCategory category : CATEGORIES) {
            setItem(category.slot(), createCategoryItem(category.material(), "§c" + category.reason(), category.line1(), category.line2()),
                    (player, _) -> handleReportSubmit(player, category.reason()));
        }

        setCloseButton(49);
    }

    private void renderTargetHead() {
        ItemStack headItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(targetName)
                .setDisplayName("§d" + targetName)
                .addLore(
                        "§8/report " + targetName, "",
                        " §7This is the player you are ",
                        " §7reporting to online staff.",
                        ""
                )
                .addItemFlag(ItemFlag.HIDE_PROFILE)
                .toItemStack();

        setItem(4, headItem);
    }

    private ItemStack createCategoryItem(Material material, String title, String line1, String line2) {
        return new ItemBuilder(material)
                .setDisplayName(title)
                .addLore(line1, line2,
                        "",
                        "§eClick to select."
                )
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .toItemStack();
    }

    private void handleReportSubmit(Player reporter, String categoryReason) {
        ReportManager.getInstance().submitAndNotify(reporter, targetName, categoryReason, attachedMessage);
        close();
    }
}
