package dreamyr.eventplugin.managers;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.effects.CombatEffect;
import dreamyr.eventplugin.effects.EffectScheduler;
import dreamyr.eventplugin.util.RewardSystem;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CombatZoneManager {

    private final EventPlugin plugin;
    private final Map<String, CombatZone> zones = new HashMap<>();
    private final Map<String, Set<UUID>> zoneParticipants = new HashMap<>();

    public CombatZoneManager(EventPlugin plugin) {
        this.plugin = plugin;
        loadZones();
    }

    // --- Робота бою ---
    public void startCombat(String zoneKey) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null) return;

        Set<UUID> uuids = zoneParticipants.getOrDefault(zoneKey, Collections.emptySet());
        Set<Player> players = new HashSet<>();
        for (UUID uuid : uuids) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) players.add(p);
        }

        runWave(zoneKey, 0, players);
    }

    private void runWave(String zoneKey, int index, Set<Player> players) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null || index >= zone.getWaves().size()) {
            finishCombat(zoneKey, players);
            return;
        }

        CombatWave wave = zone.getWaves().get(index);
        int count = wave.getMobAmount() + (players.size() - 1) * zone.getScalePerPlayer();

        // Спавн мобів
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < count; i++) {
                Location spawn = zone.getSpawnLocation();
                LivingEntity e = (LivingEntity) spawn.getWorld().spawnEntity(spawn, wave.getMobType());
                e.setCustomName("§cМоб хвилі");
            }
        });

        // Накладення ефектів
        EffectScheduler scheduler = new EffectScheduler(plugin);
        for (Player p : players) {
            scheduler.scheduleEffects(zone.getEffects(), p);
        }

        // Наступна хвиля
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> runWave(zoneKey, index + 1, players),
                wave.getDelayTicks()
        );
    }

    private void finishCombat(String zoneKey, Set<Player> players) {
        CombatZone zone = zones.get(zoneKey);
        if (zone == null) return;

        for (Player p : players) {
            RewardSystem.giveReward(p, zone.getRewards());
            p.sendMessage("§aВи перемогли в CombatZone: " + zoneKey);
        }
        zoneParticipants.remove(zoneKey);
    }

    public void addParticipant(String zoneKey, UUID playerUuid) {
        zoneParticipants.computeIfAbsent(zoneKey, k -> new HashSet<>()).add(playerUuid);
    }

    // --- Завантаження ---
    public void loadZones() {
        FileConfiguration cfg = plugin.getConfig();
        zones.clear();
        if (!cfg.contains("combatzones")) return;

        ConfigurationSection root = cfg.getConfigurationSection("combatzones");
        for (String key : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(key);
            if (sec == null) continue;

            // Локація спавну
            World w = Bukkit.getWorld(sec.getString("world", ""));
            if (w == null) continue;
            Location spawn = new Location(
                    w,
                    sec.getDouble("x"),
                    sec.getDouble("y"),
                    sec.getDouble("z")
            );

            int scale = sec.getInt("scale_per_player", 2);

            // Завантаження хвиль
            List<CombatWave> waves = new ArrayList<>();
            ConfigurationSection ws = sec.getConfigurationSection("waves");
            if (ws != null) {
                for (String wKey : ws.getKeys(false)) {
                    ConfigurationSection wSec = ws.getConfigurationSection(wKey);
                    EntityType type = EntityType.valueOf(wSec.getString("mob").toUpperCase());
                    int amt = wSec.getInt("amount");
                    int d = wSec.getInt("delay");
                    waves.add(new CombatWave(type, amt, d));
                }
            }

            // Завантаження ефектів
            List<CombatEffect> effects = new ArrayList<>();
            ConfigurationSection es = sec.getConfigurationSection("effects");
            if (es != null) {
                for (String eKey : es.getKeys(false)) {
                    ConfigurationSection eSec = es.getConfigurationSection(eKey);
                    PotionEffect pe = deserializePotionEffect(eSec);
                    int delay = eSec.getInt("delay", 0);
                    if (pe != null) effects.add(new CombatEffect(pe, delay));
                }
            }

            // Завантаження нагород
            List<ItemStack> rewards = new ArrayList<>();
            ConfigurationSection rs = sec.getConfigurationSection("rewards");
            if (rs != null) {
                for (String rKey : rs.getKeys(false)) {
                    ItemStack is = rs.getItemStack(rKey);
                    if (is != null) rewards.add(is);
                }
            }

            zones.put(key, new CombatZone(spawn, waves, effects, rewards, scale));
        }
    }

    private PotionEffect deserializePotionEffect(ConfigurationSection s) {
        try {
            String t = s.getString("type", "").toUpperCase();
            PotionEffectType type = PotionEffectType.getByName(t);
            if (type == null) return null;
            return new PotionEffect(
                    type,
                    s.getInt("duration", 200),
                    s.getInt("amplifier", 0),
                    s.getBoolean("ambient", false),
                    s.getBoolean("particles", true),
                    s.getBoolean("icon", true)
            );
        } catch (Exception ex) {
            plugin.getLogger().warning("Помилка десеріалізації ефекту: " + ex.getMessage());
            return null;
        }
    }

    // --- Збереження ---
    public void saveZone(String key) {
        CombatZone zone = zones.get(key);
        if (zone == null) return;

        FileConfiguration cfg = plugin.getConfig();
        String base = "combatzones." + key;

        // Основні параметри
        cfg.set(base + ".world", zone.getSpawnLocation().getWorld().getName());
        cfg.set(base + ".x", zone.getSpawnLocation().getX());
        cfg.set(base + ".y", zone.getSpawnLocation().getY());
        cfg.set(base + ".z", zone.getSpawnLocation().getZ());
        cfg.set(base + ".scale_per_player", zone.getScalePerPlayer());

        // Хвилі
        cfg.set(base + ".waves", null);
        List<CombatWave> waves = zone.getWaves();
        for (int i = 0; i < waves.size(); i++) {
            CombatWave w = waves.get(i);
            String path = base + ".waves.wave" + i;
            cfg.set(path + ".mob", w.getMobType().name());
            cfg.set(path + ".amount", w.getMobAmount());
            cfg.set(path + ".delay", w.getDelayTicks());
        }

        // Ефекти
        cfg.set(base + ".effects", null);
        List<CombatEffect> effs = zone.getEffects();
        for (int i = 0; i < effs.size(); i++) {
            CombatEffect ce = effs.get(i);
            String p = base + ".effects.effect" + i;
            PotionEffect pe = ce.getEffect();
            cfg.set(p + ".type", pe.getType().getName());
            cfg.set(p + ".duration", pe.getDuration());
            cfg.set(p + ".amplifier", pe.getAmplifier());
            cfg.set(p + ".ambient", pe.isAmbient());
            cfg.set(p + ".particles", pe.hasParticles());
            cfg.set(p + ".icon", pe.hasIcon());
            cfg.set(p + ".delay", ce.getDelayTicks());
        }

        // Нагороди
        cfg.set(base + ".rewards", null);
        List<ItemStack> rewards = zone.getRewards();
        for (int i = 0; i < rewards.size(); i++) {
            cfg.set(base + ".rewards.item" + i, rewards.get(i));
        }

        plugin.saveConfig();
    }

    /** Додає одну хвилю до зони */
    public void addWave(String zoneKey, CombatWave wave) {
        CombatZone zone = zones.get(zoneKey);
        if (zone != null) zone.getWaves().add(wave);
    }

    /** Додає один ефект до зони */
    public void addEffect(String zoneKey, CombatEffect effect) {
        CombatZone zone = zones.get(zoneKey);
        if (zone != null) zone.getEffects().add(effect);
    }

    /** Додає один предмет-нагороду до зони */
    public void addReward(String zoneKey, ItemStack reward) {
        CombatZone zone = zones.get(zoneKey);
        if (zone != null) zone.getRewards().add(reward);
    }

    /** Зберігає всі зони */
    public void saveAllZones() {
        plugin.getConfig().set("combatzones", null);
        for (String key : zones.keySet()) {
            saveZone(key);
        }
    }

    // --- Отримувачі ---
    public CombatZone getZone(String key) {
        return zones.get(key);
    }

    public Set<String> getZoneKeys() {
        return zones.keySet();
    }
}
