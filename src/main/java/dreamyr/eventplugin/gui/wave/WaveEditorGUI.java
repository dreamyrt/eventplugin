package dreamyr.eventplugin.gui.wave;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class WaveEditorGUI {

    private static final String TITLE_PREFIX = "🌊 Редактор хвиль: ";

    // Старий метод можна залишити для швидких тестів, але тепер основний:
    public static void open(Player player, String zoneKey) {
        Inventory gui = Bukkit.createInventory(null, 27, TITLE_PREFIX + zoneKey);

        gui.setItem(10, new ItemBuilder(Material.ZOMBIE_SPAWN_EGG)
                .setName("&aМоб: Zombie")
                .build());
        gui.setItem(12, new ItemBuilder(Material.NAME_TAG)
                .setName("&aКількість: 5")
                .build());
        gui.setItem(14, new ItemBuilder(Material.CLOCK)
                .setName("&aЗатримка: 20с")
                .build());
        gui.setItem(16, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&a➕ Додати хвилю")
                .build());

        player.openInventory(gui);
    }

    // За бажанням залишаємо для викликів без ключа
    public static void open(Player player) {
        open(player, "default");
    }
}
