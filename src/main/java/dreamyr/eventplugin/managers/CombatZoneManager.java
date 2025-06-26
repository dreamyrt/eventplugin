package dreamyr.eventplugin.managers;

import dreamyr.eventplugin.effects.CombatEffect;
import dreamyr.eventplugin.effects.EffectScheduler;
import dreamyr.eventplugin.util.RewardSystem;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CombatZoneManager {

    private final Plugin plugin;
    private final Map<String, CombatZone> zones = new HashMap<>();
    private final Map<String, Set<UUID>> zoneParticipants = new HashMap<>();

    public CombatZoneManager(Plugin plugin) {
        this.plugin = plugin;
        loadZones();
    }

    public void startCombat(String zoneKey) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null) return;

        Set<UUID> participants = zoneParticipants.getOrDefault(zoneKey, new HashSet<>());
        Set<Player> players = new HashSet<>();
        for (UUID uuid : participants) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }

        runWave(zoneKey, 0, players);
    }

    private void runWave(String zoneKey, int waveIndex, Set<Player> players) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null || waveIndex >= zone.getWaves().size()) {
            finishCombat(zoneKey, players);
            return;
        }

        CombatWave wave = zone.getWaves().get(waveIndex);
        int mobCount = wave.getMobAmount() + (players.size() - 1) * zone.getScalePerPlayer();

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < mobCount; i++) {
                Location spawn = zone.getSpawnLocation();
                LivingEntity entity = (LivingEntity) spawn.getWorld().spawnEntity(spawn, wave.getMobType());
                entity.setCustomName("§cМоб хвилі");
            }
        });

        EffectScheduler effectScheduler = new EffectScheduler(plugin);
        for (Player player : players) {
            effectScheduler.scheduleEffects(zone.getEffects(), player);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            runWave(zoneKey, waveIndex + 1, players);
        }, wave.getDelayTicks());
    }

    private void finishCombat(String zoneKey, Set<Player> players) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null) return;

        for (Player player : players) {
            RewardSystem.giveReward(player, zone.getRewards());
            player.sendMessage("§aВи перемогли в бою зоною: " + zoneKey);
        }
        zoneParticipants.remove(zoneKey);
    }

    public void addParticipant(String zoneKey, UUID player) {
        zoneParticipants.computeIfAbsent(zoneKey, k -> new HashSet<>()).add(player);
    }

    public void loadZones() {
        FileConfiguration config = plugin.getConfig();
        zones.clear();
        if (!config.contains("combatzones")) return;

        ConfigurationSection section = config.getConfigurationSection("combatzones");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection zoneSec = section.getConfigurationSection(key);
            if (zoneSec == null) continue;

            World world = Bukkit.getWorld(zoneSec.getString("world"));
            if (world == null) continue;

            Location spawn = new Location(
                    world,
                    zoneSec.getDouble("x"),
                    zoneSec.getDouble("y"),
                    zoneSec.getDouble("z")
            );

            int scalePerPlayer = zoneSec.getInt("scale_per_player", 2);

            List<CombatWave> waves = new ArrayList<>();
            ConfigurationSection wavesSec = zoneSec.getConfigurationSection("waves");
            if (wavesSec != null) {
                for (String waveKey : wavesSec.getKeys(false)) {
                    ConfigurationSection w = wavesSec.getConfigurationSection(waveKey);
                    if (w == null) continue;

                    EntityType type = EntityType.valueOf(w.getString("mob"));
                    int amount = w.getInt("amount");
                    int delay = w.getInt("delay");
                    waves.add(new CombatWave(type, amount, delay));
                }
            }

            List<CombatEffect> effects = new ArrayList<>();
            ConfigurationSection effectsSec = zoneSec.getConfigurationSection("effects");
            if (effectsSec != null) {
                for (String effKey : effectsSec.getKeys(false)) {
                    ConfigurationSection e = effectsSec.getConfigurationSection(effKey);
                    if (e == null) continue;

                    // Власна десеріалізація PotionEffect
                    PotionEffect pe = deserializePotionEffect(e);
                    int delay = e.getInt("delay", 0);
                    if (pe != null) {
                        effects.add(new CombatEffect(pe, delay));
                    }
                }
            }

            // rewards можна додати, якщо вони є у конфігу — тут просто порожній список
            zones.put(key, new CombatZone(spawn, waves, effects, new ArrayList<>(), scalePerPlayer));
        }
    }

    private PotionEffect deserializePotionEffect(ConfigurationSection section) {
        try {
            String typeName = section.getString("type");
            if (typeName == null) return null;
            PotionEffectType type = PotionEffectType.getByName(typeName.toUpperCase());
            if (type == null) return null;

            int duration = section.getInt("duration", 200);
            int amplifier = section.getInt("amplifier", 0);
            boolean ambient = section.getBoolean("ambient", false);
            boolean particles = section.getBoolean("particles", true);
            boolean icon = section.getBoolean("icon", true);

            return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
        } catch (Exception ex) {
            plugin.getLogger().warning("Не вдалося десеріалізувати PotionEffect: " + ex.getMessage());
            return null;
        }
    }

    public CombatZone getZone(String key) {
        return zones.get(key);
    }
}
