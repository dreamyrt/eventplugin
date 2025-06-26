package dreamyr.eventplugin.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.List;

public class EffectScheduler {

    private final Plugin plugin;

    public EffectScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleEffects(List<CombatEffect> effects, Player player) {
        for (CombatEffect effect : effects) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.addPotionEffect(effect.getEffect());
            }, effect.getDelayTicks());
        }
    }
}
