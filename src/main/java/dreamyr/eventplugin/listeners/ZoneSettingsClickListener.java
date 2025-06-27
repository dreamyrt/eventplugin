package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.ZoneSettingsGUI;
import dreamyr.eventplugin.managers.CombatZone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ZoneSettingsClickListener implements Listener {

    private final EventPlugin plugin;

    public ZoneSettingsClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!ZoneSettingsGUI.isZoneSettingsTitle(title)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        String zoneKey = ZoneSettingsGUI.getZoneKeyFromTitle(title);
        CombatZone zone = plugin.getCombatZoneManager().getZone(zoneKey);
        if (zone == null) {
            player.sendMessage(ChatColor.RED + "❌ Зона не знайдена.");
            return;
        }

        switch (slot) {
            case 11 -> {
                Location loc = player.getLocation();
                zone.setSpawnLocation(loc);
                player.sendMessage(ChatColor.GREEN + "📍 Точка спавну оновлена на вашу позицію.");
            }

            case 13 -> {
                int current = zone.getScalePerPlayer();
                int next = current >= 5 ? 1 : current + 1;
                zone.setScalePerPlayer(next);
                player.sendMessage(ChatColor.YELLOW + "👥 Новий коефіцієнт гравців: " + next);
                ZoneSettingsGUI.open(player, zoneKey, next); // оновити GUI
            }

            case 15 -> {
                plugin.getCombatZoneManager().saveZone(zoneKey);
                player.sendMessage(ChatColor.GREEN + "✅ Зміни збережено.");
                player.closeInventory();
            }
        }
    }
}
