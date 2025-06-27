package dreamyr.eventplugin.gui.effects;

import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class EffectEditorGUI {

    private static final String TITLE_PREFIX = "🧪 Редактор ефектів: ";

    private static final List<PotionEffectType> EFFECT_TYPES = Arrays.asList(
            PotionEffectType.SPEED,
            PotionEffectType.STRENGTH,
            PotionEffectType.HEALTH_BOOST,
            PotionEffectType.REGENERATION,
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.JUMP_BOOST
    );

    public static void open(Player player, String zoneKey, PotionEffectType currentEffect, int duration, int amplifier, int delay) {
        Inventory gui = Bukkit.createInventory(null, 27, TITLE_PREFIX + zoneKey);

        gui.setItem(10, new ItemBuilder(Material.POTION)
                .setName("&bЕфект: " + (currentEffect != null ? currentEffect.getName() : "NONE"))
                .build());
        gui.setItem(12, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setName("&bТривалість: " + duration + "с")
                .build());
        gui.setItem(14, new ItemBuilder(Material.GLOWSTONE_DUST)
                .setName("&bРівень: " + amplifier)
                .build());
        gui.setItem(16, new ItemBuilder(Material.CLOCK)
                .setName("&bЗатримка: " + delay + "с")
                .build());
        gui.setItem(22, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&a➕ Додати ефект")
                .build());

        player.openInventory(gui);
    }

    public static String getZoneKeyFromTitle(String title) {
        return title.substring(TITLE_PREFIX.length());
    }

    public static boolean isEffectEditorTitle(String title) {
        return title.startsWith(TITLE_PREFIX);
    }

    public static List<PotionEffectType> getEffectTypes() {
        return EFFECT_TYPES;
    }
}
