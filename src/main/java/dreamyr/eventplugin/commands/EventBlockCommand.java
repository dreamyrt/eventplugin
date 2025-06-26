package dreamyr.eventplugin.commands;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.EventBlockSettingsGUI;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventBlockCommand implements CommandExecutor {

    private final EventPlugin plugin;

    public EventBlockCommand(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Цю команду може виконувати лише гравець.");
            return true;
        }

        if (args.length == 0) {
            // Відкриваємо GUI налаштувань для івент-блоку під гравцем
            String eventKey = plugin.getEventBlockManager().getEventKeyByPlayer(player.getUniqueId());
            if (eventKey == null) {
                player.sendMessage("[EventPlugin] Ви не пов’язані з жодним івент-блоком.");
                return true;
            }
            EventBlockSettingsGUI gui = new EventBlockSettingsGUI(plugin.getEventBlockManager(), eventKey);
            player.openInventory(gui.getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            // Створити івент-блок, за замовчуванням combatEnabled = true
            Block target = player.getTargetBlockExact(5);
            if (target == null) {
                player.sendMessage("[EventPlugin] Поглянь на блок, який хочеш зробити івент-блоком.");
                return true;
            }
            plugin.getEventBlockManager().createEventBlock(target.getLocation(), player, true);
            player.sendMessage("[EventPlugin] Івент-блок створено з бойовим режимом. Відкриваємо GUI...");
            String eventKey = plugin.getEventBlockManager().generateKey(target.getLocation());
            EventBlockSettingsGUI gui = new EventBlockSettingsGUI(plugin.getEventBlockManager(), eventKey);
            player.openInventory(gui.getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("create_no_combat")) {
            // Створити івент-блок без бою (combatEnabled = false)
            Block target = player.getTargetBlockExact(5);
            if (target == null) {
                player.sendMessage("[EventPlugin] Поглянь на блок, який хочеш зробити івент-блоком.");
                return true;
            }
            plugin.getEventBlockManager().createEventBlock(target.getLocation(), player, false);
            player.sendMessage("[EventPlugin] Івент-блок створено без бойового режиму. Відкриваємо GUI...");
            String eventKey = plugin.getEventBlockManager().generateKey(target.getLocation());
            EventBlockSettingsGUI gui = new EventBlockSettingsGUI(plugin.getEventBlockManager(), eventKey);
            player.openInventory(gui.getInventory());
            return true;
        }

        player.sendMessage("[EventPlugin] Невідома команда. Використання: /eventblock [create | create_no_combat]");
        return true;
    }
}
