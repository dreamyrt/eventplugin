package dreamyr.eventplugin.gui.effects;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EffectEditorGUI {

    private static final String TITLE_PREFIX = "🧪 Редактор ефектів: ";

    public static void open(Player player, String zoneKey) {
        Inventory gui = Bukkit.createInventory(null, 27, TITLE_PREFIX + zoneKey);

        gui.setItem(10, new ItemBuilder(Material.POTION)
                .setName("&bЕфект: SPEED")
                .build());
        gui.setItem(12, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setName("&bТривалість: 15с")
                .build());
        gui.setItem(14, new ItemBuilder(Material.GLOWSTONE_DUST)
                .setName("&bРівень: 2")
                .build());
        gui.setItem(16, new ItemBuilder(Material.CLOCK)
                .setName("&bЗатримка: 5с")
                .build());
        gui.setItem(22, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&a➕ Додати ефект")
                .build());

        player.openInventory(gui);
    }

    public static void open(Player player) {
        open(player, "default");
    }
}
