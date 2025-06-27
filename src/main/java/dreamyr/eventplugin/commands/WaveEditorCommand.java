package dreamyr.eventplugin.commands;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.gui.wave.WaveEditorGUI;
import dreamyr.eventplugin.managers.CombatZone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaveEditorCommand implements CommandExecutor {

    private final EventPlugin plugin;

    public WaveEditorCommand(EventPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Цю команду може виконувати лише гравець.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Вкажи ID бойової зони для редагування хвиль.");
            return false;
        }

        String zoneKey = args[0];
        CombatZone zone = plugin.getCombatZoneManager().getZone(zoneKey);

        if (zone == null) {
            player.sendMessage("Бойова зона з таким ID не знайдена.");
            return true;
        }

        WaveEditorGUI.open(player, zoneKey, zone.getWaves());
        player.sendMessage("Відкрито редактор хвиль для зони: " + zoneKey);
        return true;
    }
}
