package dreamyr.eventplugin.gui;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.managers.EventBlockManager;
import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EventBlockGUI {
    private final EventPlugin plugin;
    private final EventBlockManager eventBlockManager;

    public EventBlockGUI(EventPlugin plugin) {
        this.plugin = plugin;
        this.eventBlockManager = plugin.getEventBlockManager();
    }

    public void openEventBlockGUI(Player player, String eventKey) {
        Inventory gui = Bukkit.createInventory(null, 27, "Івент-Блок: " + eventKey);

        // 🟡 Показати ресурси (слот 10)
        Map<Material, Integer> required = eventBlockManager.getRequiredResources(eventKey);
        ItemStack progressItem = buildProgressItem(required, player);
        gui.setItem(10, progressItem);

        // ✅ Підтвердити (слот 13)
        ItemStack confirm = new ItemBuilder(Material.LIME_CONCRETE)
                .setName("§aПідтвердити завершення")
                .setLore("§7Натисни ще раз, щоб підтвердити.")
                .build();
        gui.setItem(13, confirm);

        // 🔴 Завершити (слот 16)
        ItemStack finish = new ItemBuilder(Material.RED_CONCRETE)
                .setName("§cЗавершити івент")
                .setLore("§7Потребує підтвердження.")
                .build();
        gui.setItem(16, finish);

        // ⚔️ Налаштувати бойову зону (слот 22)
        ItemStack combatZone = new ItemBuilder(Material.IRON_SWORD)
                .setName("§eНалаштувати бойову зону")
                .setLore("§7Клікни, щоб створити або змінити.")
                .build();
        gui.setItem(22, combatZone);

        player.openInventory(gui);
    }

    public ItemStack buildProgressItem(Map<Material, Integer> requiredResources, Player player) {
        List<String> lore = new ArrayList<>();
        Map<Material, Integer> playerInventory = getPlayerInventorySummary(player);

        for (Map.Entry<Material, Integer> entry : requiredResources.entrySet()) {
            Material mat = entry.getKey();
            int required = entry.getValue();
            int collected = playerInventory.getOrDefault(mat, 0);

            String line = (collected >= required ? "§a" : "§c")
                    + mat.name() + ": " + collected + "/" + required;
            lore.add(line);
        }

        return new ItemBuilder(Material.CHEST)
                .setName("§eРесурси для завершення")
                .setLore(lore.toArray(new String[0]))
                .build();
    }

    private Map<Material, Integer> getPlayerInventorySummary(Player player) {
        Map<Material, Integer> result = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            result.put(item.getType(),
                    result.getOrDefault(item.getType(), 0)
                            + item.getAmount());
        }
        return result;
    }
}
