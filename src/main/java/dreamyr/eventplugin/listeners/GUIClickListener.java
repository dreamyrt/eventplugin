package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.managers.EventBlockManager;
import dreamyr.eventplugin.util.RewardSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GUIClickListener implements Listener {

    private final EventBlockManager eventBlockManager;
    private final Map<UUID, Long> confirmTimestamps = new HashMap<>();
    private static final long CONFIRM_TIMEOUT = 5000; // 5 секунд

    public GUIClickListener(EventBlockManager eventBlockManager) {
        this.eventBlockManager = eventBlockManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inventory = event.getInventory();
        if (inventory == null || !event.getView().getTitle().equals("Івент-Блок")) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot != 26) return; // Кнопка Завершити

        UUID playerId = player.getUniqueId();
        String eventKey = eventBlockManager.getEventKeyByPlayer(playerId);
        if (eventKey == null) {
            player.sendMessage("§cПомилка: немає прив’язки до івент-блоку.");
            return;
        }

        // Збір кількості ресурсів у гравця
        Map<Material, Integer> currentInv = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            currentInv.put(item.getType(), currentInv.getOrDefault(item.getType(), 0) + item.getAmount());
        }

        if (!eventBlockManager.hasEnoughResources(eventKey, currentInv)) {
            player.sendMessage("§cНедостатньо ресурсів для завершення івенту!");
            return;
        }

        long now = System.currentTimeMillis();
        if (!confirmTimestamps.containsKey(playerId) || now - confirmTimestamps.get(playerId) > CONFIRM_TIMEOUT) {
            confirmTimestamps.put(playerId, now);
            player.sendMessage("§eНатисни ще раз протягом 5 секунд для підтвердження.");
            return;
        }

        // Підтверджено
        confirmTimestamps.remove(playerId);

        // Видача нагород
        Set<UUID> participants = eventBlockManager.getParticipants(eventKey);
        List<ItemStack> rewards = eventBlockManager.getReward("default");

        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                RewardSystem.giveReward(p, rewards);
            }
        }

        eventBlockManager.clearEvent(eventKey);
        player.sendMessage("§aІвент завершено! Нагороди видано.");
        player.closeInventory();
    }
}
