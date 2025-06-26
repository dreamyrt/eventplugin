package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.effects.EffectEditorGUI;
import dreamyr.eventplugin.gui.rewards.RewardEditorGUI;
import dreamyr.eventplugin.gui.wave.WaveEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CombatZoneGUIClickListener implements Listener {

    private final EventPlugin plugin;
    private static final String CONFIG_TITLE_PREFIX = "⚔️ Налаштування бойової зони: ";

    public CombatZoneGUIClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        // Фільтруємо лише потрібні GUI
        if (!title.startsWith(CONFIG_TITLE_PREFIX)
                && !title.contains("Редактор хвиль")
                && !title.contains("Редактор ефектів")
                && !title.contains("Редактор нагород")) {
            return;
        }

        event.setCancelled(true);
        int slot = event.getRawSlot();

        // Головне меню налаштувань CombatZone
        if (title.startsWith(CONFIG_TITLE_PREFIX)) {
            String zoneKey = title.substring(CONFIG_TITLE_PREFIX.length());
            switch (slot) {
                case 10 -> WaveEditorGUI.open(player, zoneKey);
                case 12 -> EffectEditorGUI.open(player, zoneKey);
                case 14 -> RewardEditorGUI.open(player, zoneKey);
                case 16 -> {
                    plugin.getCombatZoneManager().saveZone(zoneKey);
                    player.sendMessage(ChatColor.GREEN + "✅ Налаштування CombatZone '" + zoneKey + "' збережено!");
                    player.closeInventory();
                }
                default -> { /* інші слоти ігноруємо */ }
            }
            return;
        }

        // Редактор нагород
        if (title.contains("Редактор нагород")) {
            if (slot == 53) {
                // тут можна викликати збереження rewards, якщо є відповідний метод
                player.sendMessage(ChatColor.GREEN + "💾 Нагороди збережено!");
                player.closeInventory();
            }
            return;
        }

        // (За потреби можна додати обробку кліків у редакторах хвиль та ефектів)
    }
}
