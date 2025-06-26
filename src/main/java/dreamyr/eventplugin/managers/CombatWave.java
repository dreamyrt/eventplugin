package dreamyr.eventplugin.managers;

import org.bukkit.entity.EntityType;

public class CombatWave {

    private final EntityType mobType;
    private final int mobAmount;
    private final int delayTicks;

    public CombatWave(EntityType mobType, int mobAmount, int delayTicks) {
        this.mobType = mobType;
        this.mobAmount = mobAmount;
        this.delayTicks = delayTicks;
    }

    public EntityType getMobType() {
        return mobType;
    }

    public int getMobAmount() {
        return mobAmount;
    }

    public int getDelayTicks() {
        return delayTicks;
    }
}
