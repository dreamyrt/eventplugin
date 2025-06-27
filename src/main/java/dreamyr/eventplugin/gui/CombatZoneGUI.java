package dreamyr.eventplugin.gui;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Простий інтерфейс для керування CombatZone:
 * – Запустити бій
 * – Зупинити бій
 * – Переглянути статус
 * – Інформація
 * – Редагувати хвилі
 */
public class CombatZoneGUI {

    private static final String TITLE_PREFIX = "CombatZone: ";

    /**
     * Відкриває GUI керування боєм для певної зони.
     */
    public static void open(Player player, String zoneKey) {
        // Збільшив розмір до 18, щоб додати кнопку редагування хвиль
        Inventory gui = Bukkit.createInventory(null, 18, TITLE_PREFIX + zoneKey);

        gui.setItem(2, createItem(Material.GREEN_WOOL,  ChatColor.GREEN  + "Запустити бій"));
        gui.setItem(4, createItem(Material.RED_WOOL,    ChatColor.RED    + "Зупинити бій"));
        gui.setItem(6, createItem(Material.PAPER,       ChatColor.YELLOW + "Переглянути статус"));
        gui.setItem(8, createItem(Material.COMPASS,     ChatColor.AQUA   + "Інформація"));

        // Кнопка "Редагувати хвилі" в слот 13 (можна змінити)
        gui.setItem(13, new ItemBuilder(Material.SKELETON_SKULL)
                .setName(ChatColor.GOLD + "Редагувати хвилі")
                .setLore(ChatColor.GRAY + "Натисніть, щоб відкрити редактор хвиль")
                .build());

        player.openInventory(gui);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
