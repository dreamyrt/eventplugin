package dreamyr.eventplugin.gui.shared;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ConfirmGUI {

    public static void open(Player player, String actionName) {
        Inventory gui = Bukkit.createInventory(null, 27, "✔️ Підтвердити: " + actionName);

        gui.setItem(11, new ItemBuilder(Material.LIME_CONCRETE).setName("&aТак").build());
        gui.setItem(15, new ItemBuilder(Material.RED_CONCRETE).setName("&cНі").build());

        player.openInventory(gui);
    }
}
