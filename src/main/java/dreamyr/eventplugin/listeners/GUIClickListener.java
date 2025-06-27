package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.gui.CombatZoneConfigGUI;
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
        String title = event.getView().getTitle();
        if (inventory == null || !title.startsWith("Івент-Блок: ")) return;

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        String eventKey = title.substring("Івент-Блок: ".length());

        int slot = event.getRawSlot();

        // 🔴 Завершити (слот 16)
        if (slot == 16) {
            handleFinish(player, eventKey);
            return;
        }

        // ⚔️ Налаштувати бойову зону (слот 22)
        if (slot == 22) {
            CombatZoneConfigGUI.open(player, eventKey);
        }
    }

    private void handleFinish(Player player, String eventKey) {
        // Перевірка ресурсів
        Map<Material, Integer> currentInv = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            currentInv.put(item.getType(),
                    currentInv.getOrDefault(item.getType(), 0)
                            + item.getAmount());
        }

        if (!eventBlockManager.hasEnoughResources(eventKey, currentInv)) {
            player.sendMessage("§cНедостатньо ресурсів для завершення івенту!");
            return;
        }

        // Підтвердження
        long now = System.currentTimeMillis();
        if (!confirmTimestamps.containsKey(player.getUniqueId())
                || now - confirmTimestamps.get(player.getUniqueId()) > CONFIRM_TIMEOUT) {
            confirmTimestamps.put(player.getUniqueId(), now);
            player.sendMessage("§eНатисни ще раз протягом 5 секунд для підтвердження.");
            return;
        }
        confirmTimestamps.remove(player.getUniqueId());

        // Видача нагород
        Set<UUID> participants = eventBlockManager.getParticipants(eventKey);
        List<ItemStack> rewards = eventBlockManager.getReward("default");
        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                RewardSystem.giveReward(p, rewards);
            }
        }

        // Очищення
        eventBlockManager.clearEvent(eventKey);
        player.sendMessage("§aІвент завершено! Нагороди видано.");
        player.closeInventory();
    }
}
