package dreamyr.eventplugin.managers;

import dreamyr.eventplugin.EventPlugin;
import dreamyr.eventplugin.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.util.*;
import java.util.logging.Level;

public class EventBlockManager {

    private final EventPlugin plugin;

    private final Map<String, Map<Material, Integer>> requiredResources = new HashMap<>();
    private final Map<String, Map<Material, Integer>> submittedResources = new HashMap<>();
    private final Map<String, Set<UUID>> participants = new HashMap<>();
    private final Map<UUID, String> playerEventMap = new HashMap<>();
    private final Map<String, List<ItemStack>> rewardsMap = new HashMap<>();
    private final Map<String, Boolean> isCombatEnabled = new HashMap<>();

    public EventBlockManager(EventPlugin plugin) {
        this.plugin = plugin;
    }

    public void createEventBlock(Location location, Player creator, boolean combatEnabled) {
        String key = generateKey(location);

        Map<Material, Integer> defaultReq = new HashMap<>();
        defaultReq.put(Material.DIAMOND, 5);
        defaultReq.put(Material.GOLD_INGOT, 10);

        requiredResources.put(key, defaultReq);
        submittedResources.put(key, new HashMap<>());
        participants.put(key, new HashSet<>());
        isCombatEnabled.put(key, combatEnabled);

        setPlayerEvent(creator.getUniqueId(), key);
        saveAll();
    }

    private String generateKey(Location loc) {
        return loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public Map<Material, Integer> getRequiredResources(String key) {
        return requiredResources.getOrDefault(key, Collections.emptyMap());
    }

    public Map<Material, Integer> getSubmittedResources(String key) {
        return submittedResources.getOrDefault(key, Collections.emptyMap());
    }

    public void addSubmittedResource(String key, Material mat, int amount, Player player) {
        submittedResources.computeIfAbsent(key, k -> new HashMap<>())
                .merge(mat, amount, Integer::sum);

        if (hasEnoughResources(key)) {
            if (isCombatEnabled.getOrDefault(key, true)) {
                plugin.getCombatZoneManager().startCombat(key);
            } else {
                List<ItemStack> rewards = getReward(key);
                for (ItemStack reward : rewards) {
                    player.getInventory().addItem(reward);
                }
                player.sendMessage("§aВи здали ресурси і отримали нагороду!");
                clearEvent(key);
            }
            saveAll();
        } else {
            saveAll();
        }
    }

    public boolean hasEnoughResources(String key) {
        Map<Material, Integer> required = requiredResources.get(key);
        Map<Material, Integer> submitted = submittedResources.get(key);
        if (required == null || submitted == null) return false;

        for (Map.Entry<Material, Integer> entry : required.entrySet()) {
            if (submitted.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
        }
        return true;
    }

    public boolean hasEnoughResources(String key, Map<Material, Integer> currentInventory) {
        Map<Material, Integer> required = requiredResources.get(key);
        if (required == null) return false;

        for (Map.Entry<Material, Integer> entry : required.entrySet()) {
            int have = currentInventory.getOrDefault(entry.getKey(), 0);
            if (have < entry.getValue()) return false;
        }
        return true;
    }

    public void addParticipant(String key, UUID uuid) {
        participants.computeIfAbsent(key, k -> new HashSet<>()).add(uuid);
    }

    public Set<UUID> getParticipants(String key) {
        return participants.getOrDefault(key, Collections.emptySet());
    }

    public void setPlayerEvent(UUID playerUUID, String eventKey) {
        playerEventMap.put(playerUUID, eventKey);
    }

    public String getEventKeyByPlayer(UUID playerUUID) {
        return playerEventMap.get(playerUUID);
    }

    public void removePlayerEvent(UUID playerUUID) {
        playerEventMap.remove(playerUUID);
    }

    public List<ItemStack> getReward(String key) {
        return rewardsMap.getOrDefault(key, Collections.emptyList());
    }

    public void clearEvent(String key) {
        requiredResources.remove(key);
        submittedResources.remove(key);
        participants.remove(key);
        isCombatEnabled.remove(key);
        playerEventMap.values().removeIf(val -> val.equals(key));
    }

    public void loadAll() {
        loadFromConfig();
        loadRewards();
    }

    public void saveAll() {
        saveToConfig();
    }

    public void loadFromConfig() {
        FileConfiguration config = plugin.getConfig();
        requiredResources.clear();
        submittedResources.clear();
        participants.clear();
        isCombatEnabled.clear();

        if (!config.contains("eventblocks")) return;
        ConfigurationSection blocks = config.getConfigurationSection("eventblocks");
        if (blocks == null) return;

        for (String key : blocks.getKeys(false)) {
            try {
                ConfigurationSection reqSec = blocks.getConfigurationSection(key + ".required");
                Map<Material, Integer> reqMap = new HashMap<>();
                if (reqSec != null) {
                    for (String matName : reqSec.getKeys(false)) {
                        Material mat = Material.matchMaterial(matName);
                        int amount = reqSec.getInt(matName);
                        if (mat != null) reqMap.put(mat, amount);
                    }
                }
                requiredResources.put(key, reqMap);

                ConfigurationSection subSec = blocks.getConfigurationSection(key + ".submitted");
                Map<Material, Integer> subMap = new HashMap<>();
                if (subSec != null) {
                    for (String matName : subSec.getKeys(false)) {
                        Material mat = Material.matchMaterial(matName);
                        int amount = subSec.getInt(matName);
                        if (mat != null) subMap.put(mat, amount);
                    }
                }
                submittedResources.put(key, subMap);

                List<String> uuidList = blocks.getStringList(key + ".participants");
                Set<UUID> uuidSet = new HashSet<>();
                for (String uuidStr : uuidList) {
                    try {
                        uuidSet.add(UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException ignored) {
                        plugin.getLogger().warning("Некоректний UUID: " + uuidStr + " для івент-блоку " + key);
                    }
                }
                participants.put(key, uuidSet);

                boolean combat = blocks.getBoolean(key + ".combat_enabled", true);
                isCombatEnabled.put(key, combat);

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Помилка при завантаженні івент-блоку: " + key, e);
            }
        }
    }

    public void saveToConfig() {
        FileConfiguration config = plugin.getConfig();
        config.set("eventblocks", null);

        for (String key : requiredResources.keySet()) {
            Map<Material, Integer> req = requiredResources.get(key);
            for (Map.Entry<Material, Integer> entry : req.entrySet()) {
                config.set("eventblocks." + key + ".required." + entry.getKey().name(), entry.getValue());
            }

            Map<Material, Integer> sub = submittedResources.getOrDefault(key, new HashMap<>());
            for (Map.Entry<Material, Integer> entry : sub.entrySet()) {
                config.set("eventblocks." + key + ".submitted." + entry.getKey().name(), entry.getValue());
            }

            Set<UUID> parts = participants.getOrDefault(key, new HashSet<>());
            List<String> uuidList = parts.stream().map(UUID::toString).toList();
            config.set("eventblocks." + key + ".participants", uuidList);

            boolean combat = isCombatEnabled.getOrDefault(key, true);
            config.set("eventblocks." + key + ".combat_enabled", combat);
        }

        plugin.saveConfig();
    }

    public void loadRewards() {
        FileConfiguration config = plugin.getConfig();
        rewardsMap.clear();

        if (!config.contains("eventblock_rewards")) return;

        for (String key : config.getConfigurationSection("eventblock_rewards").getKeys(false)) {
            List<?> items = config.getList("eventblock_rewards." + key);
            List<ItemStack> rewardItems = new ArrayList<>();
            if (items == null) continue;

            for (Object o : items) {
                if (!(o instanceof Map<?, ?> map)) continue;

                String matStr = (String) map.get("item");
                Material mat = Material.matchMaterial(matStr);
                if (mat == null) continue;

                int amount = map.get("amount") instanceof Integer i ? i : 1;
                String name = (String) map.getOrDefault("name", null);
                List<String> lore = (List<String>) map.getOrDefault("lore", null);

                ItemBuilder builder = new ItemBuilder(mat).amount(amount);
                if (name != null) builder.setName(name);
                if (lore != null) builder.setLore(lore.toArray(new String[0]));
                rewardItems.add(builder.build());
            }

            rewardsMap.put(key, rewardItems);
        }
    }
}