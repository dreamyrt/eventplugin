package dreamyr.eventplugin.commands;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.managers.CombatZoneManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CombatZoneCommand implements CommandExecutor {

    private final EventPlugin plugin;

    public CombatZoneCommand(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Цю команду може виконувати лише гравець.");
            return true;
        }

        CombatZoneManager combatZoneManager = plugin.getCombatZoneManager();

        if (args.length == 0) {
            player.sendMessage("[EventPlugin] Використання: /combatzone <start|stop|status> [zoneKey]");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("start")) {
            if (args.length < 2) {
                player.sendMessage("[EventPlugin] Вкажіть ключ CombatZone для запуску бою.");
                return true;
            }
            String zoneKey = args[1];
            if (combatZoneManager.getZone(zoneKey) == null) {
                player.sendMessage("[EventPlugin] CombatZone з таким ключем не знайдена.");
                return true;
            }
            combatZoneManager.startCombat(zoneKey);
            player.sendMessage("[EventPlugin] Бій у CombatZone " + zoneKey + " запущено.");
            return true;
        }

        if (sub.equals("stop")) {
            // TODO: Реалізувати зупинку бою, якщо є логіка зупинки
            player.sendMessage("[EventPlugin] Команда зупинки бою поки що не реалізована.");
            return true;
        }

        if (sub.equals("status")) {
            if (args.length < 2) {
                player.sendMessage("[EventPlugin] Вкажіть ключ CombatZone для перегляду статусу.");
                return true;
            }
            String zoneKey = args[1];
            if (combatZoneManager.getZone(zoneKey) == null) {
                player.sendMessage("[EventPlugin] CombatZone з таким ключем не знайдена.");
                return true;
            }
            // TODO: Вивести інформацію про стан CombatZone (хвилі, учасники)
            player.sendMessage("[EventPlugin] Статус CombatZone " + zoneKey + ": (TODO - інформація поки відсутня)");
            return true;
        }

        player.sendMessage("[EventPlugin] Невідома команда. Використання: /combatzone <start|stop|status> [zoneKey]");
        return true;
    }
}
