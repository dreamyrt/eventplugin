package dreamyr.eventplugin.gui.wave;

import dreamyr.eventplugin.managers.CombatWave;
import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WaveEditorGUI {

    private static final String TITLE_PREFIX = "🌊 Редактор хвиль: ";
    private static final int SIZE = 27;

    public static void open(Player player, String zoneKey, List<CombatWave> waves) {
        Inventory gui = Bukkit.createInventory(null, SIZE, TITLE_PREFIX + zoneKey);

        for (int i = 0; i < waves.size() && i * 3 < SIZE - 1; i++) {
            CombatWave wave = waves.get(i);

            // Слот для моба (i * 3)
            gui.setItem(i * 3, createMobEgg(wave.getMobType(), i));

            // Слот для кількості (i * 3 + 1)
            gui.setItem(i * 3 + 1, createAmountItem(wave.getMobAmount(), i));

            // Слот для затримки (i * 3 + 2)
            gui.setItem(i * 3 + 2, createDelayItem(wave.getDelayTicks(), i));
        }

        // Кнопка додавання нової хвилі у останньому слоті
        gui.setItem(SIZE - 1, new ItemBuilder(Material.LIME_CONCRETE)
                .setName(ChatColor.GREEN + "➕ Додати хвилю")
                .build());

        player.openInventory(gui);
    }

    private static ItemStack createMobEgg(EntityType type, int waveIndex) {
        return new ItemBuilder(Material.ZOMBIE_SPAWN_EGG)
                .setName(ChatColor.AQUA + "Моб: " + type.name())
                .setLore(
                        ChatColor.GRAY + "Хвиля #" + (waveIndex + 1),
                        ChatColor.GRAY + "Клікни, щоб змінити моба"
                )
                .build();
    }

    private static ItemStack createAmountItem(int amount, int waveIndex) {
        return new ItemBuilder(Material.NAME_TAG)
                .setName(ChatColor.AQUA + "Кількість: " + amount)
                .setLore(
                        ChatColor.GRAY + "Хвиля #" + (waveIndex + 1),
                        ChatColor.GRAY + "Клікни, щоб змінити кількість"
                )
                .build();
    }

    private static ItemStack createDelayItem(int delayTicks, int waveIndex) {
        int delaySeconds = delayTicks / 20;
        return new ItemBuilder(Material.CLOCK)
                .setName(ChatColor.AQUA + "Затримка: " + delaySeconds + " с")
                .setLore(
                        ChatColor.GRAY + "Хвиля #" + (waveIndex + 1),
                        ChatColor.GRAY + "Клікни, щоб змінити затримку"
                )
                .build();
    }

    // Додані статичні методи для перевірки заголовку GUI та отримання ключа зони

    public static boolean isWaveEditorTitle(String title) {
        return title != null && title.startsWith(TITLE_PREFIX);
    }

    public static String getZoneKeyFromTitle(String title) {
        if (isWaveEditorTitle(title)) {
            return title.substring(TITLE_PREFIX.length());
        }
        return null;
    }
}
