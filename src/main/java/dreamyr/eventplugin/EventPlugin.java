package dreamyr.eventplugin;

import dreamyr.eventplugin.commands.CombatZoneCommand;
import dreamyr.eventplugin.commands.EventBlockCommand;
import dreamyr.eventplugin.commands.WaveEditorCommand;
import dreamyr.eventplugin.listeners.*;
import dreamyr.eventplugin.managers.CombatZoneManager;
import dreamyr.eventplugin.managers.EventBlockManager;
import dreamyr.eventplugin.tasks.EventBlockParticleTask;
import org.bukkit.plugin.java.JavaPlugin;

public class EventPlugin extends JavaPlugin {

    private EventBlockManager eventBlockManager;
    private CombatZoneManager combatZoneManager;
    private WaveEditorClickListener waveEditorClickListener;

    @Override
    public void onEnable() {
        // --- Конфіг ---
        saveDefaultConfig();
        getLogger().info("📥 Конфіг завантажено або створено.");

        // --- Менеджери ---
        eventBlockManager = new EventBlockManager(this);
        combatZoneManager = new CombatZoneManager(this);
        getLogger().info("📦 Менеджери ініціалізовані.");

        // --- Слухачі ---
        getServer().getPluginManager().registerEvents(new GUIClickListener(eventBlockManager), this);

        waveEditorClickListener = new WaveEditorClickListener(this);
        getServer().getPluginManager().registerEvents(waveEditorClickListener, this);

        getServer().getPluginManager().registerEvents(new CombatZoneGUIClickListener(this), this);
        getServer().getPluginManager().registerEvents(new RewardEditorClickListener(this), this);
        getServer().getPluginManager().registerEvents(new EffectEditorClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ZoneSettingsClickListener(this), this);

        getLogger().info("🎧 Слухачі зареєстровані.");

        // --- Команди ---
        if (getCommand("eventblock") != null) {
            getCommand("eventblock").setExecutor(new EventBlockCommand(this));
        } else {
            getLogger().warning("❌ Команда 'eventblock' не знайдена у plugin.yml!");
        }

        if (getCommand("combatzone") != null) {
            getCommand("combatzone").setExecutor(new CombatZoneCommand(this));
        } else {
            getLogger().warning("❌ Команда 'combatzone' не знайдена у plugin.yml!");
        }

        if (getCommand("waveeditor") != null) {
            getCommand("waveeditor").setExecutor(new WaveEditorCommand(this));
        } else {
            getLogger().warning("❌ Команда 'waveeditor' не знайдена у plugin.yml!");
        }

        // --- Дані ---
        eventBlockManager.loadAll();
        getLogger().info("📄 Івент-блоки завантажено.");

        // --- Партикли ---
        new EventBlockParticleTask(eventBlockManager).runTaskTimer(this, 20L, 10L);
        getLogger().info("✨ Партикли активовано для івент-блоків.");

        getLogger().info("✅ EventPlugin успішно увімкнено.");
    }

    @Override
    public void onDisable() {
        eventBlockManager.saveAll();
        getLogger().info("💾 Дані збережено. EventPlugin вимкнено.");
    }

    public EventBlockManager getEventBlockManager() {
        return eventBlockManager;
    }

    public CombatZoneManager getCombatZoneManager() {
        return combatZoneManager;
    }
}
