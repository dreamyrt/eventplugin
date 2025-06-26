package dreamyr.eventplugin.gui;

import dreamyr.eventplugin.managers.CombatZoneManager;
import dreamyr.eventplugin.managers.CombatZone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CombatZoneGUI {

    private final CombatZoneManager manager;
    private final Player player;
    private Inventory inv;
    private final String zoneKey;

    public CombatZoneGUI(CombatZoneManager manager, Player player, String zoneKey) {
        this.manager = manager;
        this.player = player;
        this.zoneKey = zoneKey;

        createInventory();
    }

    private void createInventory() {
        inv = Bukkit.createInventory(null, 9, "CombatZone: " + zoneKey);

        CombatZone zone = manager.getZone(zoneKey);
        if (zone == null) {
            player.sendMessage(ChatColor.RED + "CombatZone не знайдена.");
            return;
        }

        // Кнопка старту бою
        inv.setItem(2, createItem(Material.GREEN_WOOL, ChatColor.GREEN + "Запустити бій"));

        // Кнопка зупинки бою
        inv.setItem(4, createItem(Material.RED_WOOL, ChatColor.RED + "Зупинити бій"));

        // Кнопка перегляду статусу
        inv.setItem(6, createItem(Material.PAPER, ChatColor.YELLOW + "Переглянути статус"));

        // Інформація по CombatZone (можна розширити)
        inv.setItem(8, createItem(Material.COMPASS, ChatColor.AQUA + "Інформація"));

    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public Inventory getInventory() {
        return inv;
    }

    public String getZoneKey() {
        return zoneKey;
    }
}
