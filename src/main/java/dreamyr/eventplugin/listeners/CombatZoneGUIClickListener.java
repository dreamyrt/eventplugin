package dreamyr.eventplugin.listeners;

import dreamyr.eventplugin.gui.effects.EffectEditorGUI;
import dreamyr.eventplugin.gui.rewards.RewardEditorGUI;
import dreamyr.eventplugin.gui.wave.WaveEditorGUI;
import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CombatZoneGUIClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (!title.contains("Налаштування бойової зони") &&
                !title.contains("Редактор хвиль") &&
                !title.contains("Редактор ефектів") &&
                !title.contains("Редактор нагород")) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        switch (title) {
            case "⚔️ Налаштування бойової зони" -> {
                switch (slot) {
                    case 10 -> WaveEditorGUI.open(player);
                    case 12 -> EffectEditorGUI.open(player);
                    case 14 -> RewardEditorGUI.open(player);
                    case 16 -> player.sendMessage("✅ Збережено!");
                }
            }
            case "🎁 Редактор нагород" -> {
                if (slot == 53) player.sendMessage("💾 Нагороди збережено!");
            }
        }
    }
}
