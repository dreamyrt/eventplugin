package dreamyr.eventplugin.managers;

import org.bukkit.entity.EntityType;

public class CombatWave {

    private EntityType mobType;
    private int mobAmount;
    private int delayTicks;

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

    // ✅ Додай ці методи для підтримки редагування
    public void setMobType(EntityType mobType) {
        this.mobType = mobType;
    }

    public void setMobAmount(int mobAmount) {
        this.mobAmount = mobAmount;
    }

    public void setDelayTicks(int delayTicks) {
        this.delayTicks = delayTicks;
    }
}
