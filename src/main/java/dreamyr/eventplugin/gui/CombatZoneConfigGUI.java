package dreamyr.eventplugin.gui;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CombatZoneConfigGUI {

    private static final String TITLE_PREFIX = "⚔️ Налаштування бойової зони: ";

    /**
     * Відкриває інтерфейс налаштування бойової зони для конкретного ключа.
     * @param player гравець, що відкриває GUI
     * @param zoneKey ключ бойової зони
     */
    public static void open(Player player, String zoneKey) {
        Inventory gui = Bukkit.createInventory(null, 27, TITLE_PREFIX + zoneKey);

        gui.setItem(10, new ItemBuilder(Material.ZOMBIE_SPAWN_EGG)
                .setName("&eРедагувати хвилі")
                .build());
        gui.setItem(12, new ItemBuilder(Material.POTION)
                .setName("&bРедагувати ефекти")
                .build());
        gui.setItem(14, new ItemBuilder(Material.CHEST)
                .setName("&6Редагувати нагороди")
                .build());
        gui.setItem(16, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&aЗберегти")
                .build());

        player.openInventory(gui);
    }

    /**
     * Для сумісності: відкриває GUI без ключа (zoneKey = "default").
     */
    public static void open(Player player) {
        open(player, "default");
    }
}
