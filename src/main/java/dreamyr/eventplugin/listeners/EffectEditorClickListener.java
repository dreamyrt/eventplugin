package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.effects.CombatEffect;
import dreamyr.eventplugin.gui.effects.EffectEditorGUI;
import dreamyr.eventplugin.managers.CombatZoneManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class EffectEditorClickListener implements Listener {

    private final EventPlugin plugin;

    // Тимчасово зберігаємо стан для кожного гравця, що редагує ефект (щоб тримати параметри)
    private final Map<Player, EffectData> editingEffects = new HashMap<>();

    public EffectEditorClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEffectEditorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!EffectEditorGUI.isEffectEditorTitle(title)) return;

        event.setCancelled(true);

        String zoneKey = EffectEditorGUI.getZoneKeyFromTitle(title);

        EffectData data = editingEffects.getOrDefault(player, new EffectData());

        int slot = event.getRawSlot();

        switch (slot) {
            case 10 -> { // Тип ефекту: переключаємо на наступний тип в списку
                data.selectedEffect = nextEffectType(data.selectedEffect);
                player.sendMessage(ChatColor.AQUA + "Тип ефекту: " + data.selectedEffect.getName());
            }
            case 12 -> { // Тривалість (5, 10, 15, 20)
                data.durationSeconds = nextDuration(data.durationSeconds);
                player.sendMessage(ChatColor.AQUA + "Тривалість: " + data.durationSeconds + "с");
            }
            case 14 -> { // Ампліфікатор (0..4)
                data.amplifier = (data.amplifier + 1) % 5;
                player.sendMessage(ChatColor.AQUA + "Рівень: " + data.amplifier);
            }
            case 16 -> { // Затримка (0..10)
                data.delaySeconds = (data.delaySeconds + 1) % 11;
                player.sendMessage(ChatColor.AQUA + "Затримка: " + data.delaySeconds + "с");
            }
            case 22 -> { // Додати ефект у CombatZone
                CombatZoneManager manager = plugin.getCombatZoneManager();
                var zone = manager.getZone(zoneKey);
                if (zone == null) {
                    player.sendMessage(ChatColor.RED + "Зона не знайдена!");
                    player.closeInventory();
                    return;
                }

                if (data.selectedEffect == null) {
                    player.sendMessage(ChatColor.RED + "Виберіть тип ефекту!");
                    return;
                }

                int durationTicks = data.durationSeconds * 20;
                int delayTicks = data.delaySeconds * 20;

                CombatEffect newEffect = new CombatEffect(
                        new PotionEffect(data.selectedEffect, durationTicks, data.amplifier, false, true, true),
                        delayTicks);

                manager.addEffect(zoneKey, newEffect);
                manager.saveZone(zoneKey);

                player.sendMessage(ChatColor.GREEN + "Ефект додано до CombatZone '" + zoneKey + "'.");

                // Закриваємо GUI і видаляємо стан
                editingEffects.remove(player);
                player.closeInventory();
                return;
            }
            default -> {
                // ігноруємо інші слоти
            }
        }

        // Заново відкриваємо GUI з оновленими параметрами
        editingEffects.put(player, data);
        EffectEditorGUI.open(player, zoneKey, data.selectedEffect, data.durationSeconds, data.amplifier, data.delaySeconds);
    }

    private PotionEffectType nextEffectType(PotionEffectType current) {
        var effectTypes = EffectEditorGUI.getEffectTypes();
        if (current == null) return effectTypes.get(0);
        int idx = effectTypes.indexOf(current);
        idx = (idx + 1) % effectTypes.size();
        return effectTypes.get(idx);
    }

    private int nextDuration(int current) {
        int[] options = {5, 10, 15, 20};
        int idx = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i] == current) {
                idx = i;
                break;
            }
        }
        idx = (idx + 1) % options.length;
        return options[idx];
    }

    // Клас для збереження стану параметрів ефекту гравця
    private static class EffectData {
        PotionEffectType selectedEffect = PotionEffectType.SPEED;
        int durationSeconds = 15;
        int amplifier = 1;
        int delaySeconds = 5;
    }
}
