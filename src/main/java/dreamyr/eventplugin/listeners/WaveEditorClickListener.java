package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.wave.WaveEditorGUI;
import dreamyr.eventplugin.managers.CombatWave;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class WaveEditorClickListener implements Listener {

    private static final String TITLE_PREFIX = "🌊 Редактор хвиль: ";
    private final EventPlugin plugin;

    public WaveEditorClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWaveEditorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!title.startsWith(TITLE_PREFIX)) return;

        event.setCancelled(true);

        String zoneKey = title.substring(TITLE_PREFIX.length());
        var manager = plugin.getCombatZoneManager();

        if (manager.getZone(zoneKey) == null) {
            player.sendMessage(ChatColor.RED + "Помилка: зона не знайдена.");
            player.closeInventory();
            return;
        }

        List<CombatWave> waves = manager.getZone(zoneKey).getWaves();
        int slot = event.getRawSlot();

        // Кнопка додавання нової хвилі (останній слот)
        if (slot == 26) {
            waves.add(new CombatWave(EntityType.ZOMBIE, 5, 20)); // дефолтна хвиля
            manager.saveZone(zoneKey);
            WaveEditorGUI.open(player, zoneKey, waves);
            player.sendMessage(ChatColor.GREEN + "Додано нову хвилю!");
            return;
        }

        // Індекси хвиль по 3 слоти: mob - amount - delay
        int waveIndex = slot / 3;
        if (waveIndex >= waves.size()) return;

        CombatWave wave = waves.get(waveIndex);

        switch (slot % 3) {
            case 0 -> { // змінити моба
                EntityType newType = cycleEntityType(wave.getMobType());
                wave.setMobType(newType);
                player.sendMessage(ChatColor.YELLOW + "Моб змінено на " + newType.name());
            }
            case 1 -> { // змінити кількість
                int newAmount = wave.getMobAmount() + 1;
                if (newAmount > 64) newAmount = 1;
                wave.setMobAmount(newAmount);
                player.sendMessage(ChatColor.YELLOW + "Кількість змінено на " + newAmount);
            }
            case 2 -> { // змінити затримку (секунди)
                int delaySeconds = wave.getDelayTicks() / 20;
                delaySeconds += 5;
                if (delaySeconds > 60) delaySeconds = 5;
                wave.setDelayTicks(delaySeconds * 20);
                player.sendMessage(ChatColor.YELLOW + "Затримка змінена на " + delaySeconds + " с");
            }
        }

        manager.saveZone(zoneKey);
        WaveEditorGUI.open(player, zoneKey, waves);
    }

    private EntityType cycleEntityType(EntityType current) {
        EntityType[] mobs = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER};
        int idx = 0;
        for (int i = 0; i < mobs.length; i++) {
            if (mobs[i] == current) {
                idx = i;
                break;
            }
        }
        idx = (idx + 1) % mobs.length;
        return mobs[idx];
    }
}
