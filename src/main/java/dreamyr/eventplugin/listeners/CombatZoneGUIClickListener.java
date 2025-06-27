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
    private static final String CONFIG_TITLE_PREFIX = "CombatZone: ";

    public CombatZoneGUIClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        int slot = event.getRawSlot();

        // Головне меню налаштувань бойової зони
        if (title.startsWith(CONFIG_TITLE_PREFIX)) {
            event.setCancelled(true);
            String zoneKey = title.substring(CONFIG_TITLE_PREFIX.length());

            switch (slot) {
                case 2 -> {
                    // TODO: Логіка запуску бою
                    player.sendMessage(ChatColor.YELLOW + "Запуск бою в зоні " + zoneKey);
                }
                case 4 -> {
                    // TODO: Логіка зупинки бою
                    player.sendMessage(ChatColor.YELLOW + "Зупинка бою в зоні " + zoneKey);
                }
                case 6 -> {
                    // TODO: Перегляд статусу
                    player.sendMessage(ChatColor.YELLOW + "Статус зони " + zoneKey);
                }
                case 8 -> {
                    // TODO: Інформація
                    player.sendMessage(ChatColor.YELLOW + "Інформація по зоні " + zoneKey);
                }
                case 13 -> {
                    // Відкриття редактора хвиль
                    var zone = plugin.getCombatZoneManager().getZone(zoneKey);
                    if (zone == null) {
                        player.sendMessage(ChatColor.RED + "Бойова зона не знайдена.");
                        player.closeInventory();
                        return;
                    }
                    player.closeInventory();
                    WaveEditorGUI.open(player, zoneKey, zone.getWaves());
                    player.sendMessage(ChatColor.GREEN + "Відкрито редактор хвиль.");
                }
            }
            return;
        }

        // Редактор хвиль
        if (WaveEditorGUI.isWaveEditorTitle(title)) {
            event.setCancelled(true);
            String zoneKey = WaveEditorGUI.getZoneKeyFromTitle(title);

            // Твої умови обробки кліків у редакторі хвиль
            if (slot == 26) {
                player.sendMessage(ChatColor.GREEN + "✅ Хвиля додана до '" + zoneKey + "' (заглушка).");
                player.closeInventory();
                plugin.getCombatZoneManager().saveZone(zoneKey);
            }
            return;
        }

        // Редактор ефектів
        if (EffectEditorGUI.isEffectEditorTitle(title)) {
            event.setCancelled(true);
            String zoneKey = EffectEditorGUI.getZoneKeyFromTitle(title);

            if (slot == 22) {
                player.sendMessage(ChatColor.GREEN + "✅ Ефект додано до '" + zoneKey + "' (заглушка).");
                player.closeInventory();
                plugin.getCombatZoneManager().saveZone(zoneKey);
            }
            return;
        }

        // Редактор нагород
        if (RewardEditorGUI.isRewardEditorTitle(title)) {
            event.setCancelled(true);
            String zoneKey = RewardEditorGUI.getZoneKeyFromTitle(title);

            if (slot == 53) {
                player.sendMessage(ChatColor.GREEN + "💾 Нагороди для '" + zoneKey + "' збережено (заглушка).");
                player.closeInventory();
                plugin.getCombatZoneManager().saveZone(zoneKey);
            }
        }
    }
}
