package dreamyr.eventplugin.tasks;

import dreamyr.eventplugin.managers.EventBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class EventBlockParticleTask extends BukkitRunnable {

    private final EventBlockManager manager;

    public EventBlockParticleTask(EventBlockManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Map.Entry<String, Block> entry : manager.getEventBlocks().entrySet()) {
            Block block = entry.getValue();
            if (block == null || !block.getType().isSolid()) continue;

            Location loc = block.getLocation().add(0.5, 1, 0.5);
            block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 3, 0.2, 0.2, 0.2, 0);
        }
    }
}
