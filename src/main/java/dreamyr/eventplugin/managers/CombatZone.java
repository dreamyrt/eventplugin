package dreamyr.eventplugin.managers;

import dreamyr.eventplugin.effects.CombatEffect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CombatZone {

    private final Location spawnLocation;
    private final List<CombatWave> waves;
    private final List<CombatEffect> effects;
    private final List<ItemStack> rewards;
    private final int scalePerPlayer;

    public CombatZone(Location spawnLocation, List<CombatWave> waves, List<CombatEffect> effects, List<ItemStack> rewards, int scalePerPlayer) {
        this.spawnLocation = spawnLocation;
        this.waves = waves;
        this.effects = effects;
        this.rewards = rewards;
        this.scalePerPlayer = scalePerPlayer;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public List<CombatWave> getWaves() {
        return waves;
    }

    public List<CombatEffect> getEffects() {
        return effects;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public int getScalePerPlayer() {
        return scalePerPlayer;
    }
}
