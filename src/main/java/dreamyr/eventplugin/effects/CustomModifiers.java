package dreamyr.eventplugin.effects;

import org.bukkit.Location;
import org.bukkit.entity.Parrot;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class CustomModifiers {

    private final Plugin plugin;

    public CustomModifiers(Plugin plugin) {
        this.plugin = plugin;
    }

    public void spawnParrotSwarm(Location location) {
        Random rand = new Random();
        for (int i = 0; i < 5 + rand.nextInt(6); i++) {
            location.getWorld().spawn(location, Parrot.class);
        }
    }
}