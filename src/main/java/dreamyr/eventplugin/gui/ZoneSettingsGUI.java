package dreamyr.eventplugin.gui;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ZoneSettingsGUI {

    private static final String TITLE_PREFIX = "⚙️ Налаштування зони: ";

    public static void open(Player player, String zoneKey, int scalePerPlayer) {
        Inventory gui = Bukkit.createInventory(null, 27, TITLE_PREFIX + zoneKey);

        gui.setItem(11, new ItemBuilder(Material.PLAYER_HEAD)
                .setName(ChatColor.AQUA + "📍 Встановити спавн тут")
                .setLore(ChatColor.GRAY + "Поточна позиція гравця буде точкою спавну")
                .build());

        gui.setItem(13, new ItemBuilder(Material.REPEATER)
                .setName(ChatColor.YELLOW + "👥 Коефіцієнт гравців: " + scalePerPlayer)
                .setLore(ChatColor.GRAY + "Клікни для зміни від 1 до 5")
                .build());

        gui.setItem(15, new ItemBuilder(Material.LIME_CONCRETE)
                .setName(ChatColor.GREEN + "✅ Зберегти зміни")
                .build());

        player.openInventory(gui);
    }

    public static String getZoneKeyFromTitle(String title) {
        return title.substring(TITLE_PREFIX.length());
    }

    public static boolean isZoneSettingsTitle(String title) {
        return title.startsWith(TITLE_PREFIX);
    }
}
