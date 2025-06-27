package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.rewards.RewardEditorGUI;
import dreamyr.eventplugin.managers.CombatZoneManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardEditorClickListener implements Listener {

    private final EventPlugin plugin;

    public RewardEditorClickListener(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRewardEditorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        if (!RewardEditorGUI.isRewardEditorTitle(title)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();

        // Зберегти нагороди по кнопці (слот 53)
        if (slot == 53) {
            String zoneKey = RewardEditorGUI.getZoneKeyFromTitle(title);
            CombatZoneManager manager = plugin.getCombatZoneManager();
            var zone = manager.getZone(zoneKey);

            if (zone == null) {
                player.sendMessage(ChatColor.RED + "Зона не знайдена.");
                player.closeInventory();
                return;
            }

            // Зчитуємо всі айтеми з GUI, крім кнопки збереження
            List<ItemStack> rewards = new ArrayList<>();
            for (int i = 0; i < 53; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != org.bukkit.Material.AIR) {
                    rewards.add(item);
                }
            }

            zone.getRewards().clear();
            zone.getRewards().addAll(rewards);
            manager.saveZone(zoneKey);

            player.sendMessage(ChatColor.GREEN + "💾 Нагороди збережено для CombatZone '" + zoneKey + "'.");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onRewardEditorClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        if (!RewardEditorGUI.isRewardEditorTitle(title)) return;

        String zoneKey = RewardEditorGUI.getZoneKeyFromTitle(title);
        CombatZoneManager manager = plugin.getCombatZoneManager();
        var zone = manager.getZone(zoneKey);

        if (zone == null) return;

        // При закритті інвентаря автоматично оновлюємо нагороди з GUI
        List<ItemStack> rewards = new ArrayList<>();
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                rewards.add(item);
            }
        }

        zone.getRewards().clear();
        zone.getRewards().addAll(rewards);
        manager.saveZone(zoneKey);

        player.sendMessage(ChatColor.YELLOW + "Нагороди оновлено при закритті редактора.");
    }
}
