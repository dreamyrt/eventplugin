package dreamyr.eventplugin.gui.rewards;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RewardEditorGUI {

    private static final String TITLE_PREFIX = "🎁 Редактор нагород: ";

    public static void open(Player player, String zoneKey) {
        Inventory gui = Bukkit.createInventory(null, 54, TITLE_PREFIX + zoneKey);

        gui.setItem(53, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&a💾 Зберегти нагороди")
                .build());

        player.openInventory(gui);
    }

    public static void open(Player player) {
        open(player, "default");
    }
}
