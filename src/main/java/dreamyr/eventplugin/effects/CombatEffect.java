package dreamyr.eventplugin.effects;

import org.bukkit.potion.PotionEffect;

public class CombatEffect {

    private final PotionEffect effect;
    private final int delayTicks;

    public CombatEffect(PotionEffect effect, int delayTicks) {
        this.effect = effect;
        this.delayTicks = delayTicks;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    public int getDelayTicks() {
        return delayTicks;
    }
}