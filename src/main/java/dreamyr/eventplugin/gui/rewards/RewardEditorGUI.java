package dreamyr.eventplugin.gui.rewards;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RewardEditorGUI {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, "🎁 Редактор нагород");

        gui.setItem(53, new ItemBuilder(Material.LIME_CONCRETE).setName("&a💾 Зберегти нагороди").build());

        player.openInventory(gui);
    }
}
