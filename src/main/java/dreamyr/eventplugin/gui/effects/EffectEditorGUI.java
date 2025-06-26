package dreamyr.eventplugin.gui.effects;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EffectEditorGUI {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "🧪 Редактор ефектів");

        gui.setItem(10, new ItemBuilder(Material.POTION).setName("&bЕфект: SPEED").build());
        gui.setItem(12, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName("&bТривалість: 15с").build());
        gui.setItem(14, new ItemBuilder(Material.GLOWSTONE_DUST).setName("&bРівень: 2").build());
        gui.setItem(16, new ItemBuilder(Material.CLOCK).setName("&bЗатримка: 5с").build());
        gui.setItem(22, new ItemBuilder(Material.LIME_CONCRETE).setName("&a➕ Додати ефект").build());

        player.openInventory(gui);
    }
}
